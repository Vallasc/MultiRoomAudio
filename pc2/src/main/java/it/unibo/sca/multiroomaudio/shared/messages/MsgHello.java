package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg{
    private int deviceType; // 0 client, 1 speaker, 2 client listening only
    private String mac;


    public MsgHello(int deviceType, String mac) {
        super("HELLO");
        this.deviceType = deviceType;
        this.mac = mac;
    }

    public String getMac() {
        return mac;
    }

    public int getDeviceType() {
        return deviceType;
    }

    
}
