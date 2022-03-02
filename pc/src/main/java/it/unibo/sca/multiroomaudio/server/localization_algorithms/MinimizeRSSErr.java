package it.unibo.sca.multiroomaudio.server.localization_algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.FingerprintAnalyzer;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MinimizeRSSErr extends FingerprintAnalyzer{

    public MinimizeRSSErr(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm);
    }

    private double compute(double x, double mu){
        return Math.pow(x - mu, 2);
    }

    private double[] roomError(Room r, ScanResult[] onlines){
        
        if(onlines == null){
            return null;
        }

        if(r.getNScan() == 0)
            return null;
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

        //if mse
        for(int j = 0; j < roomErr.length; j++)
            roomErr[j] = Math.sqrt(roomErr[j]);
        Arrays.sort(roomErr);
        
        return roomErr;

    }

    @Override
    public ImmutablePair<String, double[]> findRoomKey() {

        List<Room> rooms = dbm.getClientRooms(client.getId());
        if(rooms == null){
            System.out.println("rooms is null");
            return null;
        } 
        if(rooms.size()<=0) {
            System.out.println("rooms is empty");
            return null;
        }
        String roomId = null;
        double min = MAX_VALUE;
        double[] appArrRet = null;
        ScanResult[] onlines = client.getFingerprints();
        for(Room room : rooms) {
            double[] appArr = roomError(room, onlines);
            if(appArr != null){
                double app = appArr[0];
                if(app == -1d) return null;
                if(min>app){
                    min = app;
                    roomId = room.getId(); 
                    appArrRet = appArr;  
                }
            }
        }
        System.out.println("Room: " + roomId);
        return new ImmutablePair<String, double[]>(roomId, appArrRet);
    }   
    
    protected void printResults(Room r, double[] roomErr) {
        System.out.println("ERRORS:");
        for(int j = 0; j < roomErr.length; j++)
            System.out.println("\t" + roomErr[j]);
        System.out.println(r.getId() + " Min: " + roomErr[0]);
    }
}
