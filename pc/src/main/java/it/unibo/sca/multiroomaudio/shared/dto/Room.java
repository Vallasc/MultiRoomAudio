package it.unibo.sca.multiroomaudio.shared.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.github.vallasc.APInfo;

//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    String id;
    final HashMap<String, List<ScanResult>> fingerprints;//<bssid, 
    
    public Room(String id){
        fingerprints = new HashMap<>();
    }

    public Room(String id, HashMap<String, List<ScanResult>> fingerprints) {
        this.id = id;
        this.fingerprints = fingerprints;
    }

    public void setNewClient(String clientId){
    }
    
    public String getId(){
        return id;
    }

    public synchronized void putClientFingerprints(APInfo[] scans){
        //server tenerli ordinati?
        List<ScanResult> list;
        for(APInfo ap : scans){
            list = fingerprints.get(ap.getBSSID());
            if(list == null){
                List<ScanResult> results = new ArrayList<>();
                results.add(new ScanResult(ap.getBSSID(), ap.getSSID(), ap.getSignal(), ap.getFrequency(), System.currentTimeMillis()));
                fingerprints.put(ap.getBSSID(), results);
            }else{
                list.add(new ScanResult(ap.getBSSID(), ap.getSSID(), ap.getSignal(), ap.getFrequency(), System.currentTimeMillis()));
            }
        }
    }

    public void printFingerprints() {
        System.out.println(fingerprints.keySet().size());
    }
}
