package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    private final String id;
    private final HashMap<String, List<ScanResult>> fingerprints;//<bssid, 
    private int nscan;

    public Room(String id){
        this.id = id;
        this.nscan = 0;
        fingerprints = new HashMap<>();
    }

    public Room(String id, HashMap<String, List<ScanResult>> fingerprints) {
        this.nscan = 0;
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
    /*public synchronized void putClientFingerprints(ScanResult scans){
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
    }*/

    public synchronized void putClientFingerprints(ScanResult result){
        List<ScanResult> list;
            list = fingerprints.get(result.getBSSID());
            if(list == null){
                List<ScanResult> results = new ArrayList<>();
                results.add(result);
                fingerprints.put(result.getBSSID(), results);
            }else{
                list.add(result);
            }
    }

    public synchronized void setNScan(int nscan){
        this.nscan = nscan;
    }

    public int getNScan(){
        return nscan;
    }

    public void printFingerprints() {
        for(String bssid:fingerprints.keySet()){
            System.out.println(bssid + " ");
            for(int i = 0; i <fingerprints.get(bssid).size(); i++){
                System.out.println("\t" + fingerprints.get(bssid).get(i).getSignal() + " " + fingerprints.get(bssid).get(i).getMSQ());
            }
        }
        //System.out.println(fingerprints.keySet().size());
    }

    public String[] getBSSID() {
        return (String[])fingerprints.keySet().toArray();
    }
}
