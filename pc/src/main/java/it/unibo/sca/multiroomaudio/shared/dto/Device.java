package it.unibo.sca.multiroomaudio.shared.dto;


import io.github.vallasc.APInfo;

public class Device {
    
    private final int type; // 0 client, 1 speaker, 2 listeing client
    private final String ip;
    private String mac;
    private APInfo[] fingerprints;

    public Device(int type, String mac, String ip) {
        this.type = type;
        this.mac = mac;
        this.ip = ip;
    }

    public Device(int type, String ip) {
        this.type = type;
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
    }

    public int getType() {
        return type;
    }

    public synchronized void setFingerprints(APInfo[] fingerprints) {
        this.fingerprints = fingerprints;
    }

    public synchronized APInfo[] getFingerprints() {
        return fingerprints;
    }

    public void setMac(String mac){
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(this.ip);
    }
}