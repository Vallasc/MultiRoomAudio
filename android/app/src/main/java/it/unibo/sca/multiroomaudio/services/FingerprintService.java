package it.unibo.sca.multiroomaudio.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import it.unibo.sca.multiroomaudio.MainActivity;
import it.unibo.sca.multiroomaudio.R;
import it.unibo.sca.multiroomaudio.WifiHandler;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHelloBack;
import it.unibo.sca.multiroomaudio.shared.messages.MsgScanResult;
import it.unibo.sca.multiroomaudio.shared.messages.MsgStartScan;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class FingerprintService extends Service {
    private static final String TAG = FingerprintService.class.getCanonicalName();

    public static final String CHANNEL_NOTIFICATION_DEFAULT = "FingerprintServiceChannel.Default";
    private static final int ONGOING_NOTIFICATION_ID = 2;

    public static final String ACTION_START = "FingerprintService.Start";

    private final Gson gson = new Gson();
    private WifiHandler wifiHandler;

    private Socket socket;
    private boolean isScanResultsReady;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction() == ACTION_START) {
            Log.d(TAG, "SERVICE STARTED");

            // Draw notification
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                createNotificationChannel();
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new Notification.Builder(this, CHANNEL_NOTIFICATION_DEFAULT)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .build();
                startForeground(ONGOING_NOTIFICATION_ID, notification);
            }
            startScanning(intent);
            return START_STICKY;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, filter);

        wifiHandler = new WifiHandler(this);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        Log.d(TAG,"Close socket");
        try {
            socket.close();
        } catch (IOException e) {}
        super.onDestroy();
    }


    private void startScanning(Intent intent) {
        isScanResultsReady = false;
        Log.d(TAG,"Start fingerprint service");
        new Thread(()-> {
            MsgHelloBack msgHelloBack = null;
            String serverAddress = intent.getStringExtra(MainActivity.SERVER_ADDRESS);
            int socketPort = intent.getIntExtra(MainActivity.SOCKET_PORT, -1);
            int webServerPort = intent.getIntExtra(MainActivity.WEB_SERVER_PORT, -1);
            int webMusicPort = intent.getIntExtra(MainActivity.WEB_MUSIC_PORT, -1);

            DataOutputStream dOut = null;
            DataInputStream dIn = null;
            try {
                socket = new Socket(serverAddress, socketPort);
                dOut = new DataOutputStream(socket.getOutputStream());
                dIn = new DataInputStream(socket.getInputStream());
                dOut.writeUTF(gson.toJson(new MsgHello(0, intent.getStringExtra(MainActivity.ID_HOST))));
                String json = dIn.readUTF();
                msgHelloBack = gson.fromJson(json, MsgHelloBack.class);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e1) {}
                socket = null;
                Log.e(TAG, "Server socket error");
            }
            if(msgHelloBack != null && !msgHelloBack.isRejected()) {
                String url = "http://" + serverAddress + ":" + webServerPort
                        + "?type=client&id=" + msgHelloBack.getClientId() + "&wPort=" + webServerPort + "&mPort=" + webMusicPort;
                sendUrlToMainActivity(url);
            }

            if( socket == null) {
                Log.e(TAG, "Socket NULL");
                return;
            }

            Log.d(TAG,"Fingerprint service: RUNNING");
            //send this through the socket
            Gson gson = new Gson();
            while (true) {
                try {
                    String json = dIn.readUTF();
                    MsgStartScan msg = gson.fromJson(json, MsgStartScan.class);
                    //if stop read again
                    if(msg.getStart()){
                        isScanResultsReady = false;
                        boolean error = !wifiHandler.startScan();
                        if(error){
                            Log.e(TAG, "ERROR scanning wifi");
                            sleep(2000);
                        } else {
                            Log.d(TAG, "Start wifi scanning");
                        }
                        while(!isScanResultsReady && !error){ // Wait until data is ready
                            sleep(200);
                        }
                        try{
                            isScanResultsReady = false;
                            ScanResult[] results = wifiHandler.getApResults();
                            MsgScanResult msgResult = new MsgScanResult(results);
                            dOut.writeUTF( gson.toJson(msgResult) );
                            Log.d(TAG,"Result sended");
                        } catch(com.google.gson.JsonSyntaxException e) {
                            Log.e(TAG,"Message parsing error");
                            continue;
                        }
                        dOut.flush();
                    } else {
                        Log.e(TAG, "NOT STARTED SCAN");
                        sleep(500);
                    }
                } catch ( IOException e) {
                    Log.e(TAG,"Disconnected");
                    e.printStackTrace();
                    break;
                }
            }

            try {
                socket.close();
            } catch (IOException e) {}
            // Stop service
            this.stopSelf();
        }).start();
    }

    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {}
    }

    private void sendUrlToMainActivity(String url) {
        Log.e(TAG,"Sending to activity: " + url);
        Intent intent = new Intent(MainActivity.SEND_WEB_SERVER_URL);
        intent.putExtra(MainActivity.COMPLETE_WEB_PATH, url);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private BroadcastReceiver wifiReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == "android.net.wifi.SCAN_RESULTS") {
                isScanResultsReady = true;
            }
        }
    };

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_NOTIFICATION_DEFAULT,
                    "MultiroomAudio Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
