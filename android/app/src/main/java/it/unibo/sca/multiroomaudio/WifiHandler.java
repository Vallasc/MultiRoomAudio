package it.unibo.sca.multiroomaudio;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiHandler {
    private final String tag = WifiHandler.class.getCanonicalName();

    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private JavaScriptInterface jsInterface;

    public WifiHandler(){
        wifiBroadcastReceiver = new WifiBroadcastReceiver(this);
    }

    public void setJsInterface(JavaScriptInterface jsInterface) {
        this.jsInterface = jsInterface;
    }

    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(new WifiBroadcastReceiver(this),filter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(wifiBroadcastReceiver);
    }

    public void startScan(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean result = wifiManager.startScan();
        Log.d(tag,"Wifi start scan: " + result);
    }

    public void sendListOfAP(Context context) {
        StringBuilder sb = new StringBuilder();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        Log.d(tag,"Wifi Details " + wifiManager.getScanResults().size());
        for (ScanResult result : results) {
            //Log.d(tag, result.BSSID + result.SSID + result.level);
            sb.append(result.BSSID + ' ' + result.SSID + ' ' + result.level + '\n');
        }
        jsInterface.callJs("setList(\"" + sb.toString().replaceAll("\n","<br>") + "\")");
    }
}
