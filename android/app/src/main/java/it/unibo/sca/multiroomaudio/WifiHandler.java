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
    private final Context context;
    private final WifiManager wifiManager;

    public WifiHandler(Context context){
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean startScan() {
        boolean result = wifiManager.startScan();
        Log.d(tag,"Wifi start scan: " + result);
        return result;
    }

    public Fingerprint getFingerprint() {
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
