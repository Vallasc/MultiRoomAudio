package it.unibo.sca.multiroomaudio.shared.model;

public class ScanResult {
    private final String BSSID;
    private final String SSID;
    private final double signal;//mean
    private final double msq;
    private final double frequency;
    private final long timestamp;

    public ScanResult(String BSSID, String SSID, double signal, double frequency, long timestamp, double msq) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.msq = msq;
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

    public double getMSQ(){
        return this.msq;
    }
}
