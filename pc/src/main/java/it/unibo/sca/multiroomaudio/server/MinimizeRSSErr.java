package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MinimizeRSSErr extends FingerprintAnalyzer{
    private boolean exp;
    public MinimizeRSSErr(SpeakerManager speakerManager, Client client, DatabaseManager dbm, boolean exp) {
        super(speakerManager, client, dbm);
        this.exp = exp;
    }

    private double roomError(Room r){
        //max 4 reference points for each room
        ScanResult[] onlines = client.getFingerprints();
        if(onlines == null){
            return -1d;
        }
        double roomErr = 0; 
        double min = MAX_VALUE;

        for(ScanResult online : onlines){
            ArrayList<ScanResult> offlines = r.getFingerprints(online.getBSSID());
           // System.out.println("ONLINE: " + online.getBSSID() + " signal: " + online.getSignal());
            if(offlines != null){
                double minOffline = -100d;
                for(ScanResult offline : offlines){
                    if (minOffline < offline.getSignal())
                        minOffline = offline.getSignal();
                    //System.out.println("\t" + offline.getSignal() + " " + roomErr[i]);
                    
                }
                roomErr += Math.pow(online.getSignal() - minOffline, 2);
            }   
        }
        double root = Math.sqrt(roomErr);
        return root;
    }

    @Override
    public String findRoomKey() {
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
        for(Room room : rooms) {
            double app = roomError(room);
            if(app == -1d) return null;
            if(min>app){
                //System.out.println("min err: " + app + " room: " + room.getId());
                min = app;
                roomId = room.getId();                
            }
        }
        return roomId;
    }

    private double normalize(double signalStrength){
        double ret = positiveRepresentation(signalStrength);
        return (exp) ? exponentialRepresentation(ret) : ret;
    }
    
}
