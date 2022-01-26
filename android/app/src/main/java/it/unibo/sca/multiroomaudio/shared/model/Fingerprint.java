package it.unibo.sca.multiroomaudio.shared.model;

import java.util.HashMap;

public class Fingerprint {

    // Key = BSSID
    final HashMap<String, ScanResult> map;

    public Fingerprint() {
        map = new HashMap<String, ScanResult>();
    }

    public void add(ScanResult result){
        map.put(result.getBSSID(), result);
    }

    public HashMap<String, ScanResult> getMap(){
        return map;
    }
}
