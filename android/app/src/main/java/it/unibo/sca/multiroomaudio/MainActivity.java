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

    private WebView webView;
    private OfflinePhaseManager offlinePhaseManager;
    private JavascriptBindings javascriptBindings;
    private boolean wifiOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiOk = false;

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.loadUrl("file:///android_asset/public/index.html");

        //if(checkPermission())
        checkPermission();
        initWifi();
    }

    @Override
    public void onResume() {
        super.onResume();
        offlinePhaseManager.onResume();
    }

    @Override
    public void onPause() {
        offlinePhaseManager.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED)
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

        wifiOk = true;
    }

}