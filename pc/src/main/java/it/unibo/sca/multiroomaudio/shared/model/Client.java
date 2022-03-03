package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.List;

public class Client extends Device {
    public class SharedState{
        private String activeRoom = null;
        private List<ScanResult> currentTmpScans = new ArrayList<>();
        private int currentPositionScans = 0;
    }

    private final String ip;
    private final String mac;
    private transient ScanResult[] fingerprints;
    //is true if start is clicked, false otherwise
    private transient SharedState state;

    public Client(int type, String mac, String ip) {
        super(mac, 0);
        this.mac = mac;
        this.ip = ip;
        this.state = new SharedState();
    }

    public Client(String id) {
        super(id, 0);
        this.state = new SharedState();
        this.ip = null;
        this.mac = null;
    }
    
    public String getIp() {
        return ip;
    }

    public void setFingerprints(ScanResult[] scans) {
        this.fingerprints = scans;
    }

    public ScanResult[] getFingerprints() {
        return fingerprints;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(this.ip);
    }

    public void setActiveRoom(String activeRoom){
        state.activeRoom = activeRoom;
    }

    public String getActiveRoom(){
        return state.activeRoom;
    }

    public List<ScanResult> getCurrentTmpScans() {
        return state.currentTmpScans;
    }

    public void setCurrentTmpScans(List<ScanResult> currentTmpScans) {
        state.currentTmpScans = currentTmpScans;
    }

    public int getCurrentPositionScans() {
        return state.currentPositionScans;
    }
    public void setCurrentPositionScans(int currentPositionScans) {
        state.currentPositionScans = currentPositionScans;
    }
}