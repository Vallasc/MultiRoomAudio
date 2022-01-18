package it.unibo.sca.multiroomaudio.shared.model;

import io.github.vallasc.APInfo;

public class ScanResult implements Comparable{
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
    public int compareTo(Object o) {
        return ((ScanResult)o).getBSSID().compareTo(this.getBSSID());
    }

    
}
