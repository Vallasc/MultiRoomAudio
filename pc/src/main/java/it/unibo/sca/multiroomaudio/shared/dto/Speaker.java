package it.unibo.sca.multiroomaudio.shared.dto;

public class Speaker extends Device {
    private final String name;
    private boolean isMuted = false;

    public Speaker(String id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMuted(boolean isMuted) {
        this.isMuted = isMuted;
    }

    public boolean isMuted() {
        return isMuted;
    }
    
}
