package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHello extends Msg{
    private int deviceType; // 0 client, 1 speaker
    private String id;
    private String name;

    public MsgHello(int deviceType, String id, String name) {
        super("HELLO");
        this.deviceType = deviceType;
        this.id = id;
        this.name = name;
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
