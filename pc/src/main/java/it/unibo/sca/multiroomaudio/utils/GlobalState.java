package it.unibo.sca.multiroomaudio.utils;

public class GlobalState {
    private static GlobalState INSTANCE;
    private GlobalState() {}

    private int cutPower = -65;
    private int k = 5;
    private boolean useWeights = true;
    private int clientFingerprintWindowSize = 2;

    public static GlobalState getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GlobalState();
        }
        return INSTANCE;
    }

    public void setCutPower(int cutPower) {
        if(-80 <= cutPower && cutPower <= -30 ){
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
}