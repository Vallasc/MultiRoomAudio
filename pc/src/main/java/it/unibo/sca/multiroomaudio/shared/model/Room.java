package it.unibo.sca.multiroomaudio.shared.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Stanza(Nome, Map<BSSID, List<Fingerprint>>)
public class Room {
    private final String id;
    private final HashMap<String, List<ScanResult>> fingerprints;//<bssid, 
    private int nCorners;

    public Room(String id){
        this.id = id;
        this.nCorners = 0;
        this.fingerprints = new HashMap<>();
    }

    public Room(String id, HashMap<String, List<ScanResult>> fingerprints) {
        this.nCorners = 0;
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
        //nCorners = [1, maxscan]
        List<ScanResult> list;
        list = fingerprints.get(result.getBSSID());
        if(list == null){
            //here nCorners is 1 
            List<ScanResult> results = new ArrayList<>();
            if(nCorners > 1){
                for(int i = 0; i<nCorners-1; i++)
                    results.add(i, new ScanResult(result.getBSSID(), result.getSSID(), -80, result.getFrequency(), result.getTimestamp()));
            }
            results.add(nCorners-1, result);
            fingerprints.put(result.getBSSID(), results);
                
        }else{
            int len = list.size();
            if(len < nCorners)
                for(int i = len; i<nCorners-1; i++){
                    list.add(i, new ScanResult(result.getBSSID(), result.getSSID(), -80, result.getFrequency(), result.getTimestamp()));
                }
            list.add(nCorners-1, result);
        }
        /*Not a huge problem but we don't know the max number of corners so we aren't able to do this anymore
        if(nCorners == 4){
            System.out.println(result.getBSSID());
            list = fingerprints.get(result.getBSSID());
            for(ScanResult r : list){
                System.out.println("\t" + r.getSignal());
            }

        }*/
    }

    public synchronized ArrayList<ScanResult> getFingerprints(String bssid){
        return (ArrayList<ScanResult>) fingerprints.get(bssid);
    }

    public synchronized void setNCorners(int nCorners){
        this.nCorners = nCorners;
    }

    public int getNCorners(){
        return nCorners;
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
}
