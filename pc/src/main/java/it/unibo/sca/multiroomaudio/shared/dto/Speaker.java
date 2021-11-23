package it.unibo.sca.multiroomaudio.shared.dto;

public class Speaker extends Device {
    private final String name;

    public Speaker(String id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}
