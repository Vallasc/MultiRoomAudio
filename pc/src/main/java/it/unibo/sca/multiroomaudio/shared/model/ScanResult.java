package it.unibo.sca.multiroomaudio.shared.model;

import io.github.vallasc.APInfo;

public class ScanResult{
    private final String BSSID;
    private final String SSID;
    private final double signal;//mean
    private final double variance;
    private final double frequency;
    private final long timestamp;

    public ScanResult(String BSSID, String SSID, double signal, double frequency, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.variance = 0;
    }

    public ScanResult(String BSSID, String SSID, double signal, double variance, double frequency, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.variance = variance;
    }

    public ScanResult(APInfo ap) {
        this.BSSID = ap.getBSSID();
        this.SSID = ap.getSSID();
        this.signal = ap.getSignal();
        this.frequency = ap.getFrequency();
        this.timestamp = System.currentTimeMillis();
        this.variance = 0;
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

    public double getVariance(){
        return variance;
    }

    public static int compareByBSSID(ScanResult a, ScanResult b){
        return a.getBSSID().compareTo(b.getBSSID());
    }
}
