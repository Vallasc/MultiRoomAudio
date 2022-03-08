package it.unibo.sca.multiroomaudio.server.localization_algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.FingerprintAnalyzer;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;
import it.unibo.sca.multiroomaudio.utils.Utils;

public class Knn extends FingerprintAnalyzer{
    public static final int POWER_LIMIT = -70;
    private HashMap<String, Double> errors = new LinkedHashMap<>(); //<ReferencePoint, error>
    private int k = 1;
    private boolean initUnknownAP = false;
    private boolean useWeight = false;

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm, 1);
    }

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm, int k) {
        super(speakerManager, client, dbm, 1);
        this.k = k;
    }

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm, boolean initUnknownAP) {
        super(speakerManager, client, dbm, 1);
        this.initUnknownAP = initUnknownAP;
    }

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm, int k, boolean initUnknownAP, boolean useWeight) {
        super(speakerManager, client, dbm, 1);
        this.initUnknownAP = initUnknownAP;
        this.useWeight = useWeight;
        this.k = k;
    }

    public void setK(int k){
        this.k = k;
    }

    
    private HashMap<String, Double> selectK(boolean addWeights){
        // Get first k
        this.errors = Utils.sortHashMapByValueAsc(this.errors);
        double invDistance = 0;

        if(addWeights){
            // Calculate  inverse of distance
            Iterator<String> it = errors.keySet().iterator();
            for(int i = 0; it.hasNext() && i < k; i++) {
                String key = it.next();
                invDistance += 1/(Math.pow(errors.get(key), 2));
            }
        }

        // Calculate weights and classes
        HashMap<String, Double> classes = new HashMap<>();
        Iterator<String> it = errors.keySet().iterator();
        for(int i = 0; it.hasNext() && i < k; i++) {
            String key = it.next();
            String keyAggregated = key.split("_")[0];
            double weight = 1;
            if(addWeights)
                weight = (1/ Math.pow(errors.get(key), 2)) / (invDistance);
            if(classes.containsKey(keyAggregated)) 
                classes.put(keyAggregated, classes.get(keyAggregated) + weight);
            else
                classes.put(keyAggregated, weight);
        }

        return classes;
    }

    private double computePower(double x, double mu){ 
        return Math.pow(x-mu, 2);
    }

    public void computeRoomError(Room room, List<ScanResult> onlines){
        if(onlines == null || room.getNScan() == 0)
            return;

        double[] roomErr = new double[room.getNScan()];
        Arrays.fill(roomErr, 0);

        List<String> notFound = new ArrayList<>(room.getFingerprints().keySet());
        // Foreach BSSID in online fingerprint
        for(ScanResult online : onlines){
            notFound.remove(online.getBSSID());
            // Array of others RP fingerprints
            List<ScanResult> offlines = room.getFingerprints(online.getBSSID());
            if(offlines != null){
                for(int i = 0; i < offlines.size(); i++){
                    if(offlines.get(i) != null){
                        roomErr[i] += computePower(online.getSignal(), offlines.get(i).getSignal());
                    }
                }
            }
        }

        if(this.initUnknownAP){
            for(String BSSID : notFound){
                List<ScanResult> offlines = room.getFingerprints(BSSID);
                if(offlines != null){
                    for(int i=0; i< offlines.size(); i++){
                        if(offlines.get(i) != null)
                            roomErr[i] += computePower(POWER_LIMIT, offlines.get(i).getSignal());
                    }
                }
            }
        }

        for(int j = 0; j < roomErr.length; j++){
            this.errors.put(room.getId()+"_"+j, Math.sqrt(roomErr[j]));
            //System.out.println(room.getId()+"_"+j + "->" + this.errors.get(room.getId() +"_" + j) );
        }
        //Utils.sleep(4000);
    }
 
    @Override
    public String findRoomKey() {
        String roomKey = null;
        List<Room> rooms = dbm.getClientRooms(client.getId());

        if(rooms == null || rooms.size() == 0) {
            return null;
        } 

        this.errors.clear();
        List<ScanResult> onlines = client.getFingerprints();
        for(Room room : rooms) {
            computeRoomError(room, onlines);
        }
        
        HashMap<String, Double> classes = selectK(this.useWeight);

        double max = Double.MIN_VALUE;
        for(String key : classes.keySet()){
            if(classes.get(key) > max){
                max = classes.get(key);
                roomKey = key;
            }
        }

        super.printer.setKnn(classes);
        return roomKey;
    }
}
