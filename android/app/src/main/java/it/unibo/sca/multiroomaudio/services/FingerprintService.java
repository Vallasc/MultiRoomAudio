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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import it.unibo.sca.multiroomaudio.MainActivity;
import it.unibo.sca.multiroomaudio.R;
import it.unibo.sca.multiroomaudio.WifiHandler;
import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;

public class FingerprintService extends Service {
    private static final String TAG = FingerprintService.class.getCanonicalName();

    public static final String CHANNEL_NOTIFICATION_DEFAULT = "FingerprintServiceChannel.Default";
    private static final int ONGOING_NOTIFICATION_ID = 2;

    public static final String ACTION_START = "FingerprintService.Start";
    public static final String ACTION_START_CALIBRATION = "FingerprintService.Start.Calibration";
    public static final String ACTION_STOP_CALIBRATION = "FingerprintService.Stop.Calibration";

    private final Gson gson = new Gson();
    private boolean serviceStopped;
    private WifiHandler wifiHandler;
    private WifiReceiver wifiReceiver;
    private ServerConnection webSocket;


    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.d("Handlers", "Called on main thread");
            wifiHandler.startScan();
            handler.postDelayed(runnableCode, 6000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction() == ACTION_START && serviceStopped) {

            // Draw notification
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                createNotificationChannel();
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(this, 0, notificationIntent, 0);

                Notification notification = new Notification.Builder(this, CHANNEL_NOTIFICATION_DEFAULT)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentIntent(pendingIntent)
                        .build();
                startForeground(ONGOING_NOTIFICATION_ID, notification);
            }

            Log.d(TAG, "OKOKOOKOOKKOKOKOKOKKKOKOKOK");
            // wifiHandler.startScan();
            try {
                webSocket = new ServerConnection(new URI("ws://192.168.178.28/client"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            webSocket.connect();

            // Start AP scan
            handler.post(runnableCode);
            serviceStopped = false;
            return START_STICKY;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, filter);

        wifiHandler = new WifiHandler(this);

        serviceStopped = true;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        serviceStopped = true;
        if(webSocket != null && webSocket.isOpen())
            webSocket.close();
        super.onDestroy();
    }

    public void onWifiResults(){
        Fingerprint fingerprint = wifiHandler.getFingerprint();
        Log.d(TAG, "Scan done");
        for( Map.Entry entry : fingerprint.getMap().entrySet()){
            Log.d(TAG, ((Fingerprint.ScanResult) entry.getValue()).getBSSID());
        }
        if(webSocket != null && webSocket.isOpen())
            webSocket.send(fingerprint.toJson(gson));
    }

    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Receiving something");
            onWifiResults();
        }
    }

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
