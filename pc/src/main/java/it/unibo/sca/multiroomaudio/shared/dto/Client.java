package it.unibo.sca.multiroomaudio.shared.dto;

import io.github.vallasc.APInfo;

public class Client extends Device {
    private final String ip;
    private String mac;
    private APInfo[] fingerprints;
    //is true if start is clicked, false otherwise
    private boolean start = false;

    public Client(int type, String mac, String ip) {
        super(mac);
        this.mac = mac;
        this.ip = ip;
    }

    public Client( String ip) {
        super(ip); // TODO I think ipis not id
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
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

    public synchronized void setStart(boolean start){
        this.start = start;
    }

    public synchronized boolean getStart(){
        return start;
    }

    /**
     * @return: true if state changed, false otherwise
     */
    public synchronized boolean changeStart(boolean start){
        if(this.start != start){ 
            this.start = start;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(this.ip);
    }
}