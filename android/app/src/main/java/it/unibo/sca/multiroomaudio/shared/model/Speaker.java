package it.unibo.sca.multiroomaudio.shared.model;

public class Speaker extends Device {
    private String name;
    private boolean isMuted = true; //true by default
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

    public synchronized void decNumberNowPlaying() { // TODO synchronized needed?
        this.numberNowPlaying -= 1;
        if(this.numberNowPlaying == 0)
            this.isMuted = true;
    }

    public synchronized void incNumberNowPlaying() { // TODO synchronized needed?
        this.numberNowPlaying += 1;
        this.isMuted = false;
    }
    
    public synchronized int getNumberNowPlaying() { // TODO synchronized needed?
        return numberNowPlaying;
    }

}
