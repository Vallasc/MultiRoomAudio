package it.unibo.sca.multiroomaudio.shared.model;

import io.github.vallasc.APInfo;

public class ScanResult implements Comparable<ScanResult> {
    private final String BSSID;
    private final String SSID;
    private final double signal;//mean
    private final double frequency;
    private final long timestamp;

    public ScanResult(String BSSID, String SSID, double signal, double frequency, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
    }

    public ScanResult(String BSSID, String SSID, double signal, double variance, double frequency, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
    }

    public ScanResult(APInfo ap) {
        this.BSSID = ap.getBSSID();
        this.SSID = ap.getSSID();
        this.signal = ap.getSignal();
        this.frequency = ap.getFrequency();
        this.timestamp = System.currentTimeMillis();
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

    @Override
    public int compareTo(ScanResult o) {
        return ((ScanResult)o).getBSSID().compareTo(this.getBSSID());
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "BSSID='" + BSSID + '\'' +
                ", SSID='" + SSID + '\'' +
                ", signal=" + signal +
                ", frequency=" + frequency +
                ", timestamp=" + timestamp +
                '}';
    }
    
    public static int compareByBSSID(ScanResult a, ScanResult b){
        return a.getBSSID().compareTo(b.getBSSID());
    }
}
