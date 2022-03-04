package it.unibo.sca.multiroomaudio.shared.model;

public class Speaker extends Device {
    private String name;
    private transient boolean isMuted; //true by default
    private transient int numberNowPlaying = 0;

    public Speaker(String id, String name) {
        super(id, 1);
        this.name = name;
        this.isMuted = true;
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
        if(this.numberNowPlaying > 0)
            this.numberNowPlaying -= 1;
        if(this.numberNowPlaying == 0)
            this.isMuted = true;
    }

    public synchronized void incNumberNowPlaying() { 
        this.numberNowPlaying += 1;
        this.isMuted = false;
    }
    
    public synchronized int getNumberNowPlaying() { 
        return numberNowPlaying;
    }

    @Override
    public boolean equals(Object o) {
        return ((Speaker) o).getId().equals(this.getId());
    }

}
