package it.unibo.sca.multiroomaudio.shared.model;

public class ScanResult implements Comparable<Object> {
    private final String BSSID;
    private final String SSID;
    private final double signal;//mean
    private final double stddev;//standard deviation
    private final double frequency;
    private final long timestamp;

    public ScanResult(String BSSID, String SSID, double signal, double frequency, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.stddev = 0d;
    }

    public ScanResult(String BSSID, String SSID, double signal, double stddev, double frequency, long timestamp) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.signal = signal;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.stddev = stddev;
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

    public double getStddev(){
        return stddev;
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    @Override
    public int compareTo(Object o) {
        return -((ScanResult)o).getBSSID().compareTo(this.getBSSID());
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
