package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    public static final int SCAN_NOT_FOUND = 100;
    public static final int SCANS_FOR_EACH_POSITION = 4;
    public static final int MAX_POSITION = 10;
    private final String id;
    private final HashMap<String, List<ScanResult>> fingerprints; //<bssid, []>
    private int nscan; // index scan position
    private transient final ArrayList<Speaker> speakerList;
    private String urlEnter = "";
    private String urlLeave = "";

    public Room(String id){
        this.id = id;
        this.nscan = 0;
        this.fingerprints = new HashMap<>();
        this.speakerList = new ArrayList<>();
    }

    public Room(String id, HashMap<String, List<ScanResult>> fingerprints, int nscan){
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

    public void putFingerprints(ScanResult result){
        List<ScanResult> list = fingerprints.get(result.getBSSID());
        if(list == null){
            list = new ArrayList<>();
            fingerprints.put(result.getBSSID(), list);
        }
        
        // Fill
        for(Entry<String, List<ScanResult>> entry : fingerprints.entrySet()){
            System.out.println("size array in: " + entry.getValue().size());
            while(nscan > entry.getValue().size()){
                //System.out.println("bubba: " + (nscan - entry.getValue().size()));
                //System.out.println("nscan: " + nscan);
                entry.getValue().add(null);
            }
            System.out.println("size array: " + entry.getValue().size());
            if(entry.getValue() == list){
                System.out.println("TROVATO");
            }
        }
        list.set(nscan -1, result);

        /*List<ScanResult> list = fingerprints.get(result.getBSSID());
        if(list == null){
            List<ScanResult> results = new ArrayList<>();
            if(nscan > 1){
                for(int i = 0; i< nscan - 1; i++)
                    results.add(i, new ScanResult(result.getBSSID(), result.getSSID(), SCAN_NOT_FOUND, result.getStddev(), result.getFrequency(), result.getTimestamp()));
            }
            results.add( nscan - 1, result);
            fingerprints.put(result.getBSSID(), results);
                
        }else{
            int len = list.size();
            if(len < nscan)
                for(int i = len; i< nscan - 1; i++){
                    list.add(i, new ScanResult(result.getBSSID(), result.getSSID(), SCAN_NOT_FOUND, result.getStddev(), result.getFrequency(), result.getTimestamp()));
                }
            list.add(nscan-1, result);
        }*/
    }

    public ArrayList<ScanResult> getFingerprints(String bssid){
        return (ArrayList<ScanResult>) fingerprints.get(bssid);
    }

    public void incNScan(){
        this.nscan++;
    }

    public int getNScan(){
        return nscan;
    }

    public void printFingerprints() {
        System.out.println("Fingerprints:");
        for(String bssid:fingerprints.keySet()){
            System.out.println(bssid + " -> ");
            for(int i = 0; i <fingerprints.get(bssid).size(); i++){
                ScanResult fp = fingerprints.get(bssid).get(i);
                if(fp != null)
                    System.out.println("\t" + fingerprints.get(bssid).get(i).getSignal());
                else
                    System.out.println("\tnull");
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

    public List<Speaker> getSpeakerList() {
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
