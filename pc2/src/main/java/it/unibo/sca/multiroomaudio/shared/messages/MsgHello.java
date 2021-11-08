package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg {
    private int deviceType; // 0 client, 1 speaker, 2 client listening only
    private String id;

    public MsgHello(int deviceType, String id) {
        super("HELLO");
        this.deviceType = deviceType;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getDeviceType() {
        return deviceType;
    }
    
}
