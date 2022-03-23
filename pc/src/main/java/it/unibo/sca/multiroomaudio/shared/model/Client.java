package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.utils.GlobalState;
import it.unibo.sca.multiroomaudio.utils.Utils;

/**
 * Cleint object model
 */
public class Client extends Device {
    public class OfflinePhaseState{
        private boolean isOffline = false;
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

    public Client(String id) {
        super(id, 0);
        this.state = new OfflinePhaseState();
        this.fingerprintsCounter = 0;
        this.fingerprints = new ArrayList<>();
        this.oldFingerprints = new ArrayList<>();
        this.confirmationFingerprints = new ArrayList<>();
    }

    public void setFingerprints(ScanResult[] scans) {
        int FINGERPRINT_WINDOW_SIZE = GlobalState.getInstance().getClientFingerprintWindowSize();
        if(fingerprintsCounter % FINGERPRINT_WINDOW_SIZE != 0) {
            this.oldFingerprints.addAll(Arrays.asList(scans));
        } else {
            this.fingerprints = Utils.computeMeanFingeprint(this.oldFingerprints);
            this.oldFingerprints.clear();
        }
        fingerprintsCounter++;
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

    public boolean isOffline() {
        return state.isOffline;
    }

    public void setOffline(boolean isOffline) {
        state.isOffline = isOffline;
    }

    public int getFingerprintsCounter(){
        return this.fingerprintsCounter;
    }

    public void setFingerprintsCounter(int fingerprintCounter){
        this.fingerprintsCounter = fingerprintCounter;
    }
}