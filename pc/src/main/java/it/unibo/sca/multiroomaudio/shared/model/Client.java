package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.List;

public class Client extends Device {
    public class SharedState{
        private boolean start = false;
        private boolean play = false;

        // Offline phase
        private String activeRoom = null;
        private List<ScanResult> currentTmpScans = new ArrayList<>();
        private int currentPositionScans = 0;
    }

    private final String ip;
    private final String mac;
    private ScanResult[] fingerprints;
    //is true if start is clicked, false otherwise
    private SharedState state = new SharedState();

    public Client(int type, String mac, String ip) {
        super(mac);
        this.mac = mac;
        this.ip = ip;
    }

    public Client(String id) {
        super(id);
        this.ip = null;
        this.mac = null;
    }
    
    public String getIp() {
        return ip;
    }

    public synchronized void setFingerprints(ScanResult[] scans) {
        this.fingerprints = scans;
    }

    public synchronized ScanResult[] getFingerprints() {
        return fingerprints;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(this.ip);
    }

    
    //---------------------STATE THINGS------------------------------------
    /*public synchronized void setStart(boolean start, String activeRoom, int nScan){
        state.start = start;
        state.activeRoom = activeRoom;
        state.nScan = nScan;
    }*/

    public synchronized void setStart(boolean start, String activeRoom){
        state.start = start;
        state.activeRoom = activeRoom;
    }

    public synchronized boolean getStart(){
        return state.start;
    }

    /**
     * @return: true if state changed, false otherwise
     */
    public synchronized boolean changeStart(boolean start){
        if(state.start != start){ 
            state.start = start;
            return true;
        }
        return false;
    }

    public synchronized boolean getPlay(){
        return state.play;
    }

    public synchronized void setPlay(boolean play){
        if(state.start && !state.play) return;
        state.start = play;
        state.play = play;
    }

    public synchronized void setActiveRoom(String activeRoom){
        state.activeRoom = activeRoom;
    }

    public synchronized String getActiveRoom(){
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