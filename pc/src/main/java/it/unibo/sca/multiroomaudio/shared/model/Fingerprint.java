package it.unibo.sca.multiroomaudio.shared.model;

import java.util.HashMap;

import io.github.vallasc.APInfo;

public class Fingerprint {
    
    // Key = BSSID
    final HashMap<String, ScanResult> map;

    public Fingerprint() {
        map = new HashMap<String, ScanResult>();
    }

    public Fingerprint(APInfo ap){
        map = new HashMap<String, ScanResult>();
        map.put(ap.getBSSID(), new ScanResult(ap.getBSSID(), ap.getSSID(), ap.getSignal(), ap.getFrequency(), System.currentTimeMillis()));
    }

    public void add(ScanResult result){
        map.put(result.getBSSID(), result);
    }

    public HashMap<String, ScanResult> getMap(){
        return map;
    }   
}
