package it.unibo.sca.multiroomaudio.utils;

public class GlobalState {
    private static GlobalState INSTANCE;
    private GlobalState() {}

    private int cutPower = -70;
    private int k = 4;
    private boolean useWeights = false;
    private boolean confirmRoom = false;
    private int clientFingerprintWindowSize = 2;

    public static GlobalState getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GlobalState();
        }
        return INSTANCE;
    }

    public void setCutPower(int cutPower) {
        if(-80 <= cutPower && cutPower <= 0 ){
            this.cutPower = cutPower;
        }
    }

    public int getCutPower() {
        return this.cutPower;
    }
    
    public boolean getUseWeights() {
        return useWeights;
    }

    public void setUseWeights(boolean useWeights) {
        this.useWeights = useWeights;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        if(1 <= k && k <= 10 ){
            this.k = k;
        }
    }

    public int getClientFingerprintWindowSize() {
        return clientFingerprintWindowSize;
    }

    public void setClientFingerprintWindowSize(int value) {
        if(1 <= value && value <= 4 ){
            this.clientFingerprintWindowSize = value;
        }
    }

    public boolean getConfirmRoom() {
        return confirmRoom;
    }

    public void setConfirmRoom(boolean confirmRoom) {
        this.confirmRoom = confirmRoom;
    }
}
