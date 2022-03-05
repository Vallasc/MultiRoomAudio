package it.unibo.sca.multiroomaudio.server.localization_algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class Knn extends MinimizeRSSErr{
   
    private HashMap<String, Double> errors = new HashMap<>(); //<AP, error>
    private int k = 6;
    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm, 1);
    }

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm, int k) {
        super(speakerManager, client, dbm, 1);
        this.k = k;
    }

    public void setK(int k){
        this.k = k;
    }

    public static HashMap<String, Double> sortByValueAsc(Map<String, Double> map) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {

            public int compare(Map.Entry<String, Double> m1, Map.Entry<String, Double> m2) {
                return -(m2.getValue()).compareTo(m1.getValue());
            }
        });

        HashMap<String, Double> result = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static HashMap<String, Integer> sortByValueDec(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
                return -(m2.getValue()).compareTo(m1.getValue());
            }
        });

        HashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public void roomError(Room r, ScanResult[] onlines){
        if(onlines == null){
            return;
        }

        if(r.getNScan() == 0)
            return;

        double[] roomErr = new double[r.getNScan()];
        Arrays.fill(roomErr, 0);

        for(ScanResult online : onlines){
            ArrayList<ScanResult> offlines = r.getFingerprints(online.getBSSID());
            if(offlines != null){
                for(int i = 0; i < offlines.size(); i++){
                    roomErr[i] += compute(online.getSignal(), offlines.get(i).getSignal());
                }
            }
        }

        for(int j = 0; j < roomErr.length; j++){
            this.errors.put(r.getId()+"_"+j, Math.sqrt(roomErr[j]));
        }


    }
 
    @Override
    public String findRoomKey() {
        List<Room> rooms = dbm.getClientRooms(client.getId());
        if(rooms == null){
            return null;
        } 
        if(rooms.size() <= 0) {
            return null;
        }

        ScanResult[] onlines = client.getFingerprints();
        for(Room room : rooms) {
            roomError(room, onlines);
        }
        
        this.errors = sortByValueAsc(this.errors);
        HashMap<String, Integer> classes = new HashMap<>();
        Iterator<String> it = errors.keySet().iterator();
        int ki = 0;
        while(it.hasNext() && ki < k) {
            String key = it.next().split("_")[0];
            if(classes.containsKey(key)) 
                classes.put(key, classes.get(key)+1);
            else
                classes.put(key, 1);
            ki++;
        }

        classes = sortByValueDec(classes);
        super.printer.setKnn(classes);
        return classes.keySet().iterator().next();
    }
}
