package it.unibo.sca.multiroomaudio.shared.model;

/**
 * Speaker object model
 */
public class Speaker extends Device {
    private String name;
    private transient int palyingClients = 0;

    public Speaker(String id, String name) {
        super(id, 1);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isMuted() {
        return palyingClients == 0;
    }

    public void decNumberNowPlaying() {
        synchronized (this){
            if(this.palyingClients > 0)
                this.palyingClients -= 1;
        }
    }

    public void incNumberNowPlaying() {
        synchronized (this){
            this.palyingClients += 1;
        }
    }

    public int getPlayingClients() {
        return this.palyingClients;
    }

    /*public void resetPlaying(){
        synchronized (this){
            this.palyingClients = 0;
        }
    }*/

    @Override
    public boolean equals(Object o) {
        return ((Speaker) o).getId().equals(this.getId());
    }

}