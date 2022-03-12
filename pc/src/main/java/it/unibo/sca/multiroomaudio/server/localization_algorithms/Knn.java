package it.unibo.sca.multiroomaudio.server.localization_algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.FingerprintAnalyzer;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgConfirmation;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;
import it.unibo.sca.multiroomaudio.utils.GlobalState;
import it.unibo.sca.multiroomaudio.utils.Utils;

public class Knn extends FingerprintAnalyzer{
    private final int RETRY_TIME_CYCLES = 50;

    private HashMap<String, Double> errors = new LinkedHashMap<>(); //<ReferencePoint, error>
    private int k;
    private boolean initUnknownAP = false;
    private boolean useWeight = false;
    private int retryCounter = 0;
    private String oldRoomKey = null;
    private boolean confirmRoom = false;
    private HashMap<String, Double> classes = new HashMap<>();

    private boolean useGlobalVars = true;

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm);
    }

    public Knn(SpeakerManager speakerManager, Client client, DatabaseManager dbm, int k, boolean useWeight, boolean confirmRoom) {
        super(speakerManager, client, dbm);
        this.k = k;
        this.useWeight = useWeight;
        this.confirmRoom = confirmRoom;
        this.useGlobalVars = false;
    }

    private HashMap<String, Double> selectK(boolean addWeights){
        // Get first k
        this.errors = Utils.sortHashMapByValueAsc(this.errors);
        double invDistance = 0;

        if(addWeights){
            // Calculate inverse of distance
            Iterator<String> it = errors.keySet().iterator();
            for(int i = 0; it.hasNext() && i < k; i++) {
                String key = it.next();
                invDistance += 1/(Math.pow(errors.get(key), 2));
            }
        }

        // Calculate weights and classes
        this.classes = new HashMap<>();
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
            int POWER_LIMIT = GlobalState.getInstance().getCutPower() - 10;
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
    }
 
    @Override
    public String findRoomKey() {
        if(useGlobalVars){
            this. k = GlobalState.getInstance().getK();
            this.useWeight = GlobalState.getInstance().getUseWeights();
            this.confirmRoom = GlobalState.getInstance().getConfirmRoom();
        }

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

        if( ((!this.useWeight && max < (Math.floor(this.k/2) + 1) ) || (this.useWeight && max <= 0.6)) && max > 0){
            if(this.confirmRoom)
                confirmRoom(onlines, new ArrayList<>(classes.keySet()));
            roomKey = oldRoomKey;
        }

        oldRoomKey = roomKey;
        retryCounter--;

        return roomKey;
    }

    public void confirmRoom(List<ScanResult> scan, List<String> keys){
        if(client.getConfirmationFingerprints().size() != 0 || scan.size() == 0 || client.isOffline())
            return;
        if(retryCounter > 0){
            return;
        }
        retryCounter = RETRY_TIME_CYCLES;
        System.out.println("DEBUG: Confirm room");
        client.setConfirmationFP(scan);
        try {
            Session session = dbm.getClientWebSession(client.getId());
            if(session != null)
                WebSocketHandler.sendMessage(session, new MsgConfirmation(keys));
        } catch (IOException e) {}  
    }

    public void printResults(){
        System.out.println("KNN results:");
        for(var entry : this.classes.entrySet()){
            System.out.println("Room: " + entry.getKey() + ", corrispondences: " + entry.getValue());
        }
        System.out.println("");
    }
}
