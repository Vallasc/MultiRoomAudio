package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.vallasc.APInfo;

//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    private final String id;
    private final HashMap<String, List<ScanResult>> fingerprints;//<bssid, 
    
    public Room(String id){
        this.id = id;
        fingerprints = new HashMap<>();
    }

    public Room(String id, HashMap<String, List<ScanResult>> fingerprints) {
        this.id = id;
        this.fingerprints = fingerprints;
    }
    
    public String getId(){
        return id;
    }

    public int getFingerprintsSize(){
        return fingerprints.values()
                        .stream()
                        .map((scan) -> scan.size())
                        .reduce(0, (tot, element) -> tot + element);
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
