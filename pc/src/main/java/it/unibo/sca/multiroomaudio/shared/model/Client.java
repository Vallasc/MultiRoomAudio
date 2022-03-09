package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;

public class Client extends Device {
    public static final int FINGERPRINT_WINDOW_SIZE = 2;
    public class OfflinePhaseState{
        private String activeRoom = null;
        private List<ScanResult> currentTmpScans = new ArrayList<>();
        private int currentPositionScans = 0;
    }

    private final String ip;
    private final String mac;
    private transient List<ScanResult> fingerprints;
    private transient List<ScanResult> oldFingerprints;
    private transient List<ScanResult> confirmationFingerprints; //used in case the probability during the online phase is not so clear
    private transient int fingerprintsCounter;
    //is true if start is clicked, false otherwise
    private transient OfflinePhaseState state;

    public Client(int type, String mac, String ip) {
        super(mac, 0);
        this.mac = mac;
        this.ip = ip;
        this.state = new OfflinePhaseState();
        this.fingerprintsCounter = 0;
        this.fingerprints = new ArrayList<>();
        this.oldFingerprints = new ArrayList<>();
        this.confirmationFingerprints = new ArrayList<>();
    }

    public Client(String id) {
        super(id, 0);
        this.state = new OfflinePhaseState();
        this.ip = null;
        this.mac = null;
        this.fingerprintsCounter = 0;
        this.fingerprints = new ArrayList<>();
        this.oldFingerprints = new ArrayList<>();
        this.confirmationFingerprints = new ArrayList<>();
    }
    
    public String getIp() {
        return ip;
    }

    public void setFingerprints(ScanResult[] scans) {
        if(fingerprintsCounter < 3) {
            this.oldFingerprints.addAll(Arrays.asList(scans));
            fingerprintsCounter++;
        } else {
            this.fingerprints = DatabaseManager.computeMeanFingeprint(this.oldFingerprints);
            this.oldFingerprints.clear();
            fingerprintsCounter = 0;
        }
    }

    public List<ScanResult> getFingerprints() {
        return fingerprints;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public boolean equals(Object o) {
        return ((Client) o).getId().equals(this.getId());
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

    public List<ScanResult> getConfirmationFingerprints(){
        return this.confirmationFingerprints;
    }

    public void setCurrentTmpScans(List<ScanResult> currentTmpScans) {
        state.currentTmpScans = currentTmpScans;
    }

    public void setConfirmationFP(List<ScanResult> onlines){
        this.confirmationFingerprints.addAll(onlines); 
    }

    public void clearConfirmation(){
        this.confirmationFingerprints.clear();
    }

    public int getCurrentPositionScans() {
        return state.currentPositionScans;
    }
    public void setCurrentPositionScans(int currentPositionScans) {
        state.currentPositionScans = currentPositionScans;
    }
}