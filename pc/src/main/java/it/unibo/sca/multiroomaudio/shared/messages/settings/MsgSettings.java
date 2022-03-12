package it.unibo.sca.multiroomaudio.shared.messages.settings;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.utils.GlobalState;

public class MsgSettings extends Msg {
    private int filterPower;
    private int k;
    private boolean useWeights;
    private boolean confirmRoom;
    private int clientFingerprintWindowSize;

    public MsgSettings() {
        super("SETTINGS");
        GlobalState state = GlobalState.getInstance();
        this.filterPower = state.getCutPower();
        this.k = state.getK();
        this.useWeights = state.getUseWeights();
        this.confirmRoom = state.getConfirmRoom();
        this.clientFingerprintWindowSize = state.getClientFingerprintWindowSize();
    }

    public int getClientFingerprintWindowSize() {
        return clientFingerprintWindowSize;
    }

    public boolean getConfirmRoom() {
        return confirmRoom;
    }

    public boolean getUseWeights() {
        return useWeights;
    }

    public int getK() {
        return k;
    }

    public int getCutPower() {
        return filterPower;
    }

    public void saveSettings(){
        GlobalState state = GlobalState.getInstance();
        state.setCutPower(this.filterPower);
        state.setK(this.k);
        state.setUseWeights(this.useWeights);
        state.setConfirmRoom(this.confirmRoom);
        state.setClientFingerprintWindowSize(this.clientFingerprintWindowSize);
    }

    public String toString(){
        return "{filterPower: " + filterPower + 
                    ", k: " + k + 
                    ", useWeights: " + useWeights + 
                    ", confirmRoom: " + confirmRoom + 
                    ", clientFingerprintWindowSize: " + clientFingerprintWindowSize + "}";
    }
}