package it.unibo.sca.multiroomaudio.shared.model;

public abstract class Device { 
    private final String id;
    private final int type;

    public Device(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Device) {
            return ((Device) o).id.equals(this.id);
        }
        return false;
    }
}