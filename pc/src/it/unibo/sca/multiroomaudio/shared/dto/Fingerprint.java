package it.unibo.sca.multiroomaudio.shared.dto;

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

    public class ScanResult {
        private final String BSSID;
        private final String SSID;
        private final int signal;
        private final int frequency;
        private final long timestamp;

        public ScanResult(String BSSID, String SSID, int signal, int frequency, long timestamp) {
            this.BSSID = BSSID;
            this.SSID = SSID;
            this.signal = signal;
            this.frequency = frequency;
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getFrequency() {
            return frequency;
        }

        public int getSignal() {
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
