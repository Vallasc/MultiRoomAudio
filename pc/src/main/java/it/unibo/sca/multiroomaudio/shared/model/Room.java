package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    public static final int SCANS_FOR_EACH_POSITION = 4;
    public static final int MAX_POSITION = 4;
    private final String id;
    private final HashMap<String, List<ScanResult>> fingerprints;//<bssid, 
    private int nscan; // index scan position
    private final ArrayList<Speaker> speakerList;

    public Room(String id){
        this.id = id;
        this.nscan = 0;
        this.fingerprints = new HashMap<>();
        this.speakerList = new ArrayList<>();
    }

    public Room(String id, HashMap<String, List<ScanResult>> fingerprints) {
        this.nscan = 0;
        this.id = id;
        this.fingerprints = fingerprints;
        this.speakerList = new ArrayList<>();
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

    public void putClientFingerprints(ScanResult result){
        //nscan = [1, maxscan]
        List<ScanResult> list;
        list = fingerprints.get(result.getBSSID());
        if(list == null){
            List<ScanResult> results = new ArrayList<>();
            if(nscan > 1){
                for(int i = 0; i<nscan-1; i++)
                    results.add(i, new ScanResult(result.getBSSID(), result.getSSID(), -120, result.getFrequency(), result.getTimestamp()));
            }
            results.add(nscan-1, result);
            fingerprints.put(result.getBSSID(), results);
                
        }else{
            int len = list.size();
            if(len < nscan)
                for(int i = len; i<nscan-1; i++){
                    list.add(i, new ScanResult(result.getBSSID(), result.getSSID(), -120, result.getFrequency(), result.getTimestamp()));
                }
            list.add(nscan-1, result);
        }
        if(nscan == 1){
            System.out.println(result.getBSSID());
            list = fingerprints.get(result.getBSSID());
            for(ScanResult r : list){
                System.out.println("\t" + r.getSignal());
            }
        }
    }

    public ArrayList<ScanResult> getFingerprints(String bssid){
        return (ArrayList<ScanResult>) fingerprints.get(bssid);
    }

    public void setNScan(int nscan){
        this.nscan = nscan;
    }

    public int getNScan(){
        return nscan;
    }

    public void printFingerprints() {
        for(String bssid:fingerprints.keySet()){
            System.out.println(bssid + " ");
            for(int i = 0; i <fingerprints.get(bssid).size(); i++){
                System.out.println("\t" + fingerprints.get(bssid).get(i).getSignal());
            }
        }
        //System.out.println(fingerprints.keySet().size());
    }

    public String[] getBSSID() {
        String[] bssid = new String[fingerprints.keySet().size()];
        return fingerprints.keySet().toArray(bssid);
    }

    public HashMap<String, List<ScanResult>> getFingerprints() {
        return fingerprints;
    }

    public ArrayList<Speaker> getSpeakerList() {
        return speakerList;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Room)
            return this.id.equals(((Room) o).id);
        return false;
    }
}
