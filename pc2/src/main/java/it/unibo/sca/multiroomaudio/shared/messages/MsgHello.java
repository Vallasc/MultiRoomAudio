package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg{
    private int deviceType; // 0 client, 1 speaker, 2 client listening only
    private String mac;
    private boolean isSocket;


    public MsgHello(int deviceType, String mac) {
        super("HELLO");
        this.deviceType = deviceType;
        this.mac = mac;
        this.isSocket = false;
    }

    public MsgHello(int deviceType, String mac, boolean isSocket) {
        super("HELLO");
        this.deviceType = deviceType;
        this.mac = mac;
        this.isSocket = isSocket;
    }

    public boolean getIsSocket(){
        return isSocket;
    }

    public String getMac() {
        return mac;
    }

    public int getDeviceType() {
        return deviceType;
    }

    
}
