package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.List;

import io.github.vallasc.APInfo;

public class Client extends Device {
    public class SharedState{
        private boolean start = false;
        private boolean play = false;

        // Offline phase
        private String activeRoom = null;
        private List<APInfo> currentTmpScans = new ArrayList<APInfo>();
        private int currentPositionScans = 0;
    }

    private final String ip;
    private String mac;
    private ScanResult[] fingerprints;
    //is true if start is clicked, false otherwise
    private SharedState state = new SharedState();

    public Client(int type, String mac, String ip) {
        super(mac);
        this.mac = mac;
        this.ip = ip;
    }

    public Client( String ip) {
        super(ip); //TODO I think ip is not id
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
    }

    public synchronized void setFingerprints(APInfo[] aps) {
        fingerprints = new ScanResult[aps.length];
        for(int i = 0; i < aps.length; i++) {
            fingerprints[i] = new ScanResult(aps[i]);
        }
        //Arrays.sort(fingerprints);
    }

    public synchronized ScanResult[] getFingerprints() {
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
        }
        return state.start;
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


    public List<APInfo> getCurrentTmpScans() {
        return state.currentTmpScans;
    }
    public void setCurrentTmpScans(List<APInfo> currentTmpScans) {
        state.currentTmpScans = currentTmpScans;
    }

    public int getCurrentPositionScans() {
        return state.currentPositionScans;
    }
    public void setCurrentPositionScans(int currentPositionScans) {
        state.currentPositionScans = currentPositionScans;
    }
}