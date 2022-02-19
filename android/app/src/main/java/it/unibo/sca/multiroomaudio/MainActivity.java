package it.unibo.sca.multiroomaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.services.FingerprintService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final String SEND_WEB_SERVER_URL = "sendWebServerUrl";
    public static final String SERVER_ADDRESS = "serverAddress";
    public static final String ID_HOST = "idHost";
    public static final String WEB_SERVER_PORT = "webServerPort";
    public static final String WEB_MUSIC_PORT = "webMusicPort";
    public static final String SOCKET_PORT = "socketPort";
    public static final String COMPLETE_WEB_PATH = "completeWebPath";

    private Handler handler = new Handler();
    private WebView webView;
    private View chooseView;
    private Button button_speakaer;
    private Button button_client;
    private String id;

    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stopFingerprintService();

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl("file:///android_asset/public/index.html");

        chooseView = findViewById(R.id.choose_layout);

        permissionGranted = checkPermissionList();
        if(!permissionGranted) {
            grantPermissions();
        }

        button_speakaer = findViewById(R.id.button_speaker);
        button_speakaer.setOnClickListener(view -> {
            if(checkLocationEnables() && permissionGranted){
                main(false);
            }
        });

        button_client = findViewById(R.id.button_client);
        button_client.setOnClickListener(view -> {
            if(checkLocationEnables() && permissionGranted){
                main(true);
            }
        });

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        this.id = sharedPref.getString("ID", null);
        if(this.id == null) {
            String uniqueID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("ID", uniqueID);
            editor.apply();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SEND_WEB_SERVER_URL);
        LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(intentReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        stopFingerprintService();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissionList()) {
            Log.d(TAG, "Permissions granted");
            permissionGranted = true;
        } else {
            // No permission, close the app
            Log.d(TAG, "Permissions not granted, exit :(");
            Toast.makeText(this, "Unable to get permissions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received " + intent.getAction());
            if(intent.getAction() == SEND_WEB_SERVER_URL) {
                updateWebView(intent.getStringExtra(COMPLETE_WEB_PATH));
            }
        }
    };

    private boolean checkPermissionList() {
        boolean allGranted = true;
        for(String permission : getPermissionList()) {
            allGranted = allGranted &&
                    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    private List<String> getPermissionList(){
        List<String> permissionsList = new ArrayList<>();
        permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE);
        permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsList.add(Manifest.permission.INTERNET);
        return permissionsList;
    }

    private void grantPermissions(){
        ActivityCompat.requestPermissions(this, getPermissionList()
                .toArray(new String[getPermissionList().size()]),1);
    }

    private boolean checkLocationEnables() {
        boolean locationEnabled = isLocationEnabled(this);
        if(!locationEnabled){
            Toast.makeText(this, "Enable location service", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        return locationEnabled;
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }


    private void updateWebView(String url) {
        runOnUiThread(() -> webView.loadUrl(url));
        setViewInvisible();
    }

    private void setViewInvisible(){
        handler.postDelayed(() -> {
            chooseView.setVisibility(View.GONE);
        }, 1000);
    }

    private void main(boolean isClient) {
        // Find ip and port with broadcast
        new Thread(()->{
            DiscoveryService discoveryService = new DiscoveryService();
            System.out.println("DISCOVERY");
            boolean good = discoveryService.discover();
            if(good) {
                System.out.println(discoveryService.discover());
                System.out.println(discoveryService.getServerAddress());
                System.out.println(discoveryService.getWebServerPort());
                System.out.println(discoveryService.getMusicServerPort());
                System.out.println(discoveryService.getFingerprintPort());

                if(isClient) {
                    // Start fingerprint service
                    startFingerprintService(discoveryService.getServerAddress().getHostAddress(),
                            discoveryService.getWebServerPort(),
                            discoveryService.getMusicServerPort(),
                            discoveryService.getFingerprintPort());
                } else {
                    int wPort = discoveryService.getWebServerPort();
                    int mPort = discoveryService.getMusicServerPort();
                    String url = "http://" + discoveryService.getServerAddress().getHostAddress() + ":"
                                            + wPort + "?type=speaker" + "&wPort=" + wPort + "&mPort=" + mPort;
                    updateWebView(url);
                }
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Unable to contact server", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void startFingerprintService(String serverAddress, int webServerPort, int webMusicPort, int socketPort) {
        Intent intent = new Intent(this, FingerprintService.class);
        intent.setAction(FingerprintService.ACTION_START);
        intent.putExtra(SERVER_ADDRESS, serverAddress);
        intent.putExtra(ID_HOST, this.id);
        intent.putExtra(WEB_SERVER_PORT, webServerPort);
        intent.putExtra(WEB_MUSIC_PORT, webMusicPort);
        intent.putExtra(SOCKET_PORT, socketPort);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void stopFingerprintService(){
        Intent intent2 = new Intent(this, FingerprintService.class);
        stopService(intent2);
    }

}