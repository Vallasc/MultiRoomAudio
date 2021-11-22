package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgSpecs extends Msg {
    private int deviceType; // 0 client, 1 speaker
    private String MACid;

    public MsgSpecs(int deviceType, String MACid) {
        super("SPECS");
        this.deviceType = deviceType;
        this.MACid = MACid;
    }

    public String getMACid() {
        return MACid;
    }

    public int getDeviceType() {
        return deviceType;
    }
    
}
