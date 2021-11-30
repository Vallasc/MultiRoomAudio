package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg{
    private int deviceType; // 0 client, 1 speaker
    private String id;
    private String name = "Pippo"; // TODO implmentation of constructor

    public MsgHello(int deviceType, String id) {
        super("HELLO");
        this.deviceType = deviceType;
        this.id = id;
    }

    public MsgHello(int deviceType, String id, String mac) {
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

    public String getName() {
        return name;
    }
}
