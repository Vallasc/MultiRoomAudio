package it.unibo.sca.multiroomaudio.shared.model;

public class Speaker extends Device {
    private String name;
    private boolean isMuted = false; //true by default
    private int numberNowPlaying = 0;

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

    public synchronized void decNumberNowPlaying() {
        this.numberNowPlaying = this.numberNowPlaying - 1;
    }

    public synchronized void incNumberNowPlaying() {
        this.numberNowPlaying = this.numberNowPlaying + 1;
        if(numberNowPlaying == 0)
            this.isMuted = true;
    }
    
    public synchronized int getNumberNowPlaying() {
        return numberNowPlaying;
    }
}
