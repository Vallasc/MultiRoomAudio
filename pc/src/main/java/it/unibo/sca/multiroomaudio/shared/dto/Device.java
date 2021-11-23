package it.unibo.sca.multiroomaudio.shared.dto;

public abstract class Device { // TODO Needs to be renamed to Device
    private final String id;

    public Device(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return ((String) o).equals(this.id);
    }
}