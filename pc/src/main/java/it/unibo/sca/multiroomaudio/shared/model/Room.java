package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    public static final int SCANS_FOR_EACH_POSITION = 10;
    public static final int MAX_POSITION = 10;
    private final String id;
    private final ConcurrentHashMap<String, List<ScanResult>> fingerprints; //<bssid, 
    private int nscan; // index scan position
    private transient final ArrayList<Speaker> speakerList;
    private String urlEnter = "";
    private String urlLeave = "";

    public Room(String id){
        this.id = id;
        this.nscan = 0;
        this.fingerprints = new ConcurrentHashMap<>();
        this.speakerList = new ArrayList<>();
    }

    public Room(String id, ConcurrentHashMap<String, List<ScanResult>> fingerprints, int nscan){
        this.nscan = nscan;
        this.id = id;
        this.fingerprints = fingerprints;
        this.speakerList = new ArrayList<>();
    }
    
    public String getId(){
        return id;
    }

    public int getFingerprintsAPSize(){
        return fingerprints.size();
    }

    public void putClientFingerprints(ScanResult result){
        //nscan = [1, maxscan]
        List<ScanResult> list;
        list = fingerprints.get(result.getBSSID());
        if(list == null){
            List<ScanResult> results = new ArrayList<>();
            if(nscan > 1){
                for(int i = 0; i<nscan-1; i++)
                    results.add(i, new ScanResult(result.getBSSID(), result.getSSID(), result.getSignal(), result.getStddev(), result.getFrequency(), result.getTimestamp()));
            }
            results.add(nscan-1, result);
            fingerprints.put(result.getBSSID(), results);
                
        }else{
            int len = list.size();
            if(len < nscan)
                for(int i = len; i<nscan-1; i++){
                    list.add(i, new ScanResult(result.getBSSID(), result.getSSID(), result.getSignal(), result.getStddev(), result.getFrequency(), result.getTimestamp()));
                }
            list.add(nscan-1, result);
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

    public ConcurrentHashMap<String, List<ScanResult>> getFingerprints() {
        return fingerprints;
    }

    public ArrayList<Speaker> getSpeakerList() {
        return speakerList;
    }

    public String getUrlLeave() {
        return urlLeave;
    }

    public void setUrlLeave(String urlLeave) {
        this.urlLeave = urlLeave;
    }

    public String getUrlEnter() {
        return urlEnter;
    }

    public void setUrlEnter(String urlEnter) {
        this.urlEnter = urlEnter;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Room)
            return this.id.equals(((Room) o).id);
        return false;
    }
}
