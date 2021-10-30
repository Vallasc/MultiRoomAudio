package it.unibo.sca.multiroomaudio.shared.dto;

import com.google.gson.Gson;

import java.util.HashMap;

public class Fingerprint {

    private String id;
    // Key = BSSID
    private final HashMap<String, ScanResult> map;

    public Fingerprint(String id) {
        map = new HashMap<String, ScanResult>();
        this.id = id;
    }

    public Fingerprint() {
        map = new HashMap<String, ScanResult>();
        this.id = "fingerprint";
    }
    public void add(ScanResult result){
        map.put(result.getBSSID(), result);
    }

    public HashMap<String, ScanResult> getMap(){
        return map;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Fingerprint{" +
                "id='" + id + '\'' +
                ", map=" + map +
                '}';
    }

    public String toJson(Gson serializer) {
        return serializer.toJson(this);
    }

    public static class ScanResult {
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
