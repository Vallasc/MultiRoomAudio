package it.unibo.sca.multiroomaudio.shared.model;

public abstract class Device { 
    private final String id;

    public Device(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Device) {
            return ((Device) o).id.equals(this.id);
        }
        return false;
    }
}