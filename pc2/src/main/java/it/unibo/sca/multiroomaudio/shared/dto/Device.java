package it.unibo.sca.multiroomaudio.shared.dto;

import java.util.ArrayList;
import java.util.List;

import io.github.vallasc.APInfo;

public class Device {
    
    private final int type; // 0 client, 1 speaker, 2 listeing client
    private final String id;
    private String name;
    private APInfo[] fingerprints;

    public Device(int type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public synchronized void setFingerprints(APInfo[] fingerprints) {
        this.fingerprints = fingerprints;
    }

    public synchronized APInfo[] getFingerprints() {
        return fingerprints;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
