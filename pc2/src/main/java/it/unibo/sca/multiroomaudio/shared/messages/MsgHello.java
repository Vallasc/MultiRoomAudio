package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg {
    private int deviceType; // 0 client, 1 speaker
    private String MACid;

    public MsgHello(/*int deviceType, String MACid*/) {
        super(MsgTypes.HELLO);
        /*this.deviceType = deviceType;
        this.MACid = MACid;*/
    }

    public String getMACid() {
        return MACid;
    }

    public int getDeviceType() {
        return deviceType;
    }
    
}
