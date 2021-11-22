package it.unibo.sca.multiroomaudio.shared.dto;

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

    public class ScanResult {
        private final String BSSID;
        private final String SSID;
        private final double signal;
        private final double frequency;
        private final long timestamp;

        public ScanResult(String BSSID, String SSID, double signal, double frequency, long timestamp) {
            this.BSSID = BSSID;
            this.SSID = SSID;
            this.signal = signal;
            this.frequency = frequency;
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public double getFrequency() {
            return frequency;
        }

        public double getSignal() {
            return signal;
        }

        public String getBSSID() {
            return BSSID;
        }

        public String getSSID() {
            return SSID;
        }
    }
}
