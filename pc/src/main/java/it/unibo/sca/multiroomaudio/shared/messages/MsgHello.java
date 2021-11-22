package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg{
    private int deviceType; // 0 client, 1 speaker, 2 client listening only
    private String ip;
    private String mac;

    public MsgHello(int deviceType, String ip) {
        super("HELLO");
        this.deviceType = deviceType;
        this.ip = ip;
    }

    public MsgHello(int deviceType, String ip, String mac) {
        super("HELLO");
        this.deviceType = deviceType;
        this.ip = ip;
        this.mac = mac;
    }

    public String getMac(){
        return mac;
    }

    public String getIp() {
        return ip;
    }

    public int getDeviceType() {
        return deviceType;
    }

    
}
