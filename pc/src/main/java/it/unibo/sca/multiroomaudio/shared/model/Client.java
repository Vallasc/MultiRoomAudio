package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.utils.GlobalState;

public class Client extends Device {
    public class OfflinePhaseState{
        private String activeRoom = null;
        private List<ScanResult> currentTmpScans = new ArrayList<>();
        private int currentPositionScans = 0;
    }

    private transient List<ScanResult> fingerprints;
    private transient List<ScanResult> oldFingerprints;
    private transient List<ScanResult> confirmationFingerprints; //used in case the probability during the online phase is not so clear
    private transient int fingerprintsCounter;
    //is true if start is clicked, false otherwise
    private transient OfflinePhaseState state;
    private transient boolean isMoving;

    public Client(String id) {
        super(id, 0);
        this.state = new OfflinePhaseState();
        this.fingerprintsCounter = 0;
        this.fingerprints = new ArrayList<>();
        this.oldFingerprints = new ArrayList<>();
        this.confirmationFingerprints = new ArrayList<>();
        this.isMoving = false;
    }

    public void setFingerprints(ScanResult[] scans) {
        int FINGERPRINT_WINDOW_SIZE = GlobalState.getInstance().getClientFingerprintWindowSize();
        if(fingerprintsCounter <= FINGERPRINT_WINDOW_SIZE) {
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof Client)
            return ((Client) o).getId().equals(this.getId());
        return false;
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

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }
}