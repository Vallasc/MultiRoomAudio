package it.unibo.sca.multiroomaudio;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;

public class WifiHandler {
    private final String tag = WifiHandler.class.getCanonicalName();


    public WifiHandler(){}

    public void startScan(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean result = wifiManager.startScan();
        Log.d(tag,"Wifi start scan: " + result);
    }

    public Fingerprint getFingerprint(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();
        Log.d(tag,"Wifi Details " + wifiManager.getScanResults().size());
        Fingerprint fingerprint = new Fingerprint();
        for (ScanResult result : results) {
            fingerprint.add(
                    new Fingerprint.ScanResult(result.BSSID, result.SSID, result.level, result.frequency, result.timestamp));
        }
        return fingerprint;
    }
}
