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

    private final String ip;
    private final String mac;
    private transient List<ScanResult> fingerprints;
    private transient List<ScanResult> oldFingerprints;
    private transient int fingerprintsCounter;
    //is true if start is clicked, false otherwise
    private transient OfflinePhaseState state;
    private transient boolean isMoving;

    public Client(int type, String mac, String ip) {
        super(mac, 0);
        this.mac = mac;
        this.ip = ip;
        this.state = new OfflinePhaseState();
        this.fingerprintsCounter = 0;
        this.fingerprints = new ArrayList<>();
        this.oldFingerprints = new ArrayList<>();
        this.isMoving = false;
    }

    public Client(String id) {
        super(id, 0);
        this.state = new OfflinePhaseState();
        this.ip = null;
        this.mac = null;
        this.fingerprintsCounter = 0;
        this.fingerprints = new ArrayList<>();
        this.oldFingerprints = new ArrayList<>();
        this.isMoving = false;
    }
    
    public String getIp() {
        return ip;
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

    public void setCurrentTmpScans(List<ScanResult> currentTmpScans) {
        state.currentTmpScans = currentTmpScans;
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