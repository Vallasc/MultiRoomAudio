package it.unibo.sca.multiroomaudio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiHandler wifiHandler;
    WebView webView;
    OfflinePhaseManager calibrationManager;
    boolean wifiOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiOk = false;

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");



        //if(checkPermission())
        checkPermission();
        initWifi();
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiHandler.registerReceiver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        wifiHandler.unregisterReceiver(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //initWifi();
            //Print oh no
        }
    }

    private boolean checkPermission() {
        List<String> permissionsList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_WIFI_STATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.CHANGE_WIFI_STATE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),1);
            return false;
        }
        return true;
    }

    private void initWifi() {
        wifiHandler = new WifiHandler();
        JavascriptBindings jsInterface = new JavascriptBindings(this, wifiHandler);
        wifiHandler.setJsInterface(JavascriptBindings);

        webView.addJavascriptInterface(JavascriptBindings, "JSInterface");
        webView.reload();

        wifiOk = true;
        calibrationManager = new OfflinePhaseManager(wifiHandler);
    }
}