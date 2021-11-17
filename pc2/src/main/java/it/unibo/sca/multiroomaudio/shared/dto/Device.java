package it.unibo.sca.multiroomaudio.shared.dto;
public class Device {
    
    private final int type; // 0 client, 1 speaker, 2 listeing client
    private final String name;
    private final String id;

    public Device(int type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }




    public String getName() {
        return name;
    }


}
