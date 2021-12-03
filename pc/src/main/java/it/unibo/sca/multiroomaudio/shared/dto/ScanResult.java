package it.unibo.sca.multiroomaudio.shared.dto;

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
