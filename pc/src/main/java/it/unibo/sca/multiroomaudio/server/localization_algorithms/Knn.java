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
import it.unibo.sca.multiroomaudio.server.FingerprintAnalyzer;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class Knn extends FingerprintAnalyzer{
   
    private HashMap<String, Double> distances = new HashMap<>(); //<AP, error>
    private HashMap<String, String> corrispondence = new HashMap<>(); //<AP, room>
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

    public static HashMap<String, Double> sortByValue(Map<String, Double> map) {
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

    public double getMin(List<ScanResult> offlines, ScanResult online){
        double min = Double.MAX_VALUE;
        for(ScanResult offline : offlines){
            double res = Math.sqrt(Math.pow(online.getSignal() - offline.getSignal(), 2));
            if(res < min)
                min = res;
        }
        return min;
    }

    public void roomError(Room r, ScanResult[] onlines){
        if(onlines == null){
            return;
        }

        if(r.getNScan() == 0)
            return;

        int i = 0;

        int offlineSize = r.getFingerprints().keySet().size();
        String[] offlineBSSID = new String[offlineSize];

        for(String key : r.getFingerprints().keySet()){
            offlineBSSID[i] = key;
            i += 1;
        }
        Arrays.sort(onlines, new Comparator<ScanResult>(){
            public int compare(ScanResult o1, ScanResult o2){
               return o1.getBSSID().compareTo(o2.getBSSID());
            }
        });
        Arrays.sort(offlineBSSID);

        i = 0; //online index
        int j = 0; //offline index
        ScanResult online;
        ArrayList<ScanResult> offlines;
        while(i < onlines.length && j < offlineSize){
            if(onlines[i].getBSSID().compareTo(offlineBSSID[j]) == 0){//online == offline
                online = onlines[i];
                offlines = r.getFingerprints(offlineBSSID[j]);
                String app = this.corrispondence.get(offlineBSSID[j]);
                if(app != null){
                    double min = getMin(offlines, online);
                    if(min < this.distances.get(offlineBSSID[j])){
                        this.distances.put(offlineBSSID[j], min);
                        this.corrispondence.put(offlineBSSID[j], r.getId());
                    }
                }
                else{
                    this.distances.put(offlineBSSID[j], getMin(offlines, online));
                    this.corrispondence.put(offlineBSSID[j], r.getId());
                }
                i += 1;
                j += 1;
            }
            else if(onlines[i].getBSSID().compareTo(offlineBSSID[j]) < 0){//online < offline
                this.distances.putIfAbsent(onlines[i].getBSSID(), Double.MAX_VALUE);
                this.corrispondence.putIfAbsent(onlines[i].getBSSID(), r.getId());
                i += 1;
                }
            else if(onlines[i].getBSSID().compareTo(offlineBSSID[j]) > 0){//online > offline
                this.distances.putIfAbsent(offlineBSSID[j], Double.MAX_VALUE);
                this.corrispondence.putIfAbsent(offlineBSSID[j], r.getId());
                j += 1;
            }
        }

        while(i < onlines.length){
            this.distances.putIfAbsent(onlines[i].getBSSID(), Double.MAX_VALUE);
            this.corrispondence.putIfAbsent(onlines[i].getBSSID(), r.getId());
            i += 1;
        }

        while(j < offlineSize){
            this.distances.putIfAbsent(offlineBSSID[j], Double.MAX_VALUE);
            this.corrispondence.putIfAbsent(offlineBSSID[j], r.getId());
            j += 1;
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
        this.distances = sortByValue(distances);
        int ki = 0;
        HashMap<String, Integer> classCounter = new HashMap<>();
        Iterator<String> it = distances.keySet().iterator();
        while(it.hasNext() && ki < k) {
            String key = it.next();
            String y = this.corrispondence.get(key);
            if(classCounter.containsKey(y)) 
                classCounter.put(y, classCounter.get(y)+1);
            else
                classCounter.put(y, 1);
            ki++;
        }
        String roomkey = null;
        int max = Integer.MIN_VALUE;

        for(String key : classCounter.keySet()){
            if(classCounter.get(key) > max){
                max = classCounter.get(key);
                roomkey = key;
            }
        }
        super.printer.setKnn(classCounter);
        return roomkey;
    }
}
