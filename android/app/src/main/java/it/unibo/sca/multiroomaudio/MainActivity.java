package it.unibo.sca.multiroomaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sca.multiroomaudio.services.FingerprintService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();


    private WebView webView;
    private OfflinePhaseManager offlinePhaseManager;
    private JavascriptBindings javascriptBindings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermission()) {
            Log.d(TAG, "Permissions granted");
            WifiHandler wifiHandler = new WifiHandler(this); //TODO fare meglio
            // Check if geolocalization is on
            if(!wifiHandler.startScan()) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                //wifiHandler.startScan(this);
            }
            initWebView();
            startFingerprintService();

        } else {
            Log.d(TAG, "Granting permissions");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //offlinePhaseManager.onResume();
    }

    @Override
    public void onPause() {
        //offlinePhaseManager.onPause();
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
        if (requestCode == 1) {
            Log.d(TAG, "Permissions granted result");
            //initWifi();
            initWebView();
            startFingerprintService();
        }
    }

    private boolean checkPermission() {
        List<String> permissionsList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.INTERNET);
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACTIVITY_RECOGNITION);*/

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),1);
            return false;
        }
        return true;
    }

    private void initWifi() {
        offlinePhaseManager = new OfflinePhaseManager(this);
        JavascriptBindings.getInstance().setOfflinePhaseManager(offlinePhaseManager);
        JavascriptBindings.getInstance().setWebView(webView);
        webView.addJavascriptInterface(JavascriptBindings.getInstance(), "JSInterface");
        webView.reload();
    }

    private void initWebView() {
        // JavascriptBindings.getInstance().setOfflinePhaseManager(offlinePhaseManager);
        // JavascriptBindings.getInstance().setWebView(webView);

        webView = findViewById(R.id.webView);
        webView.addJavascriptInterface(JavascriptBindings.getInstance(), "JSInterface");
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/public/index.html");
    }

    public void startFingerprintService(){
        Intent intent = new Intent(this, FingerprintService.class);
        intent.setAction(FingerprintService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    public void stopFingerprintService(){
        Intent intent = new Intent(this, FingerprintService.class);
        stopService(intent);
    }
}