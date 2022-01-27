package it.unibo.sca.multiroomaudio;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class WifiHandler {
    private final String TAG = WifiHandler.class.getCanonicalName();
    private final WifiManager wifiManager;

    public WifiHandler(Context context){
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean startScan() {
        return wifiManager.startScan();
    }

    public it.unibo.sca.multiroomaudio.shared.model.ScanResult[] getApResults() {
        List<android.net.wifi.ScanResult> results = wifiManager.getScanResults();
        ScanResult[] out = new ScanResult[results.size()];
        int i = 0;
        for (android.net.wifi.ScanResult result : results) {
            out[i++] = new ScanResult(result.BSSID, result.SSID, result.level, result.frequency, result.timestamp);
        }
        return out;
    }
}
