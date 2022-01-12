package it.unibo.sca.multiroomaudio.shared.model;

import io.github.vallasc.APInfo;

public class Client extends Device {
    public class SharedState{
        private boolean start = false;
        private boolean play = false;
        private String activeRoom = null;
    }

    private final String ip;
    private String mac;
    private APInfo[] fingerprints;
    //is true if start is clicked, false otherwise
    private SharedState state = new SharedState();

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

    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(this.ip);
    }

    
    //---------------------STATE THINGS------------------------------------
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

    public synchronized String get(){
        return state.activeRoom;
    }

}