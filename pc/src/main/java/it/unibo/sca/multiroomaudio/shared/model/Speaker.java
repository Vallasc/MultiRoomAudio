package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.List;

public class Speaker extends Device {
    private String name;
    private transient boolean isMuted; //true by default
    private transient List<String> reproducingClientsId = new ArrayList<String>();

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

    public void addClient(String clientId){
        synchronized (this){
            if(!this.reproducingClientsId.contains(clientId)){
                this.reproducingClientsId.add(clientId);
                this.isMuted = false;
            }
        }
    }

    public void removeClient(String clientId){
        synchronized (this){
            this.reproducingClientsId.remove(clientId);
            if(this.reproducingClientsId.size() == 0)
                this.isMuted = true;
        }
    }

    public void resetClient(){
        this.reproducingClientsId = new ArrayList<>();
        this.isMuted = true;
    }

    public int getClientsSize(){
        return this.reproducingClientsId.size();
    }
    
    @Override
    public boolean equals(Object o) {
        return ((Speaker) o).getId().equals(this.getId());
    }

}
