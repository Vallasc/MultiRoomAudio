package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MinimizeRSSErrMin extends FingerprintAnalyzer{
    private boolean exp;
    public MinimizeRSSErrMin(SpeakerManager speakerManager, Client client, DatabaseManager dbm, boolean exp) {
        super(speakerManager, client, dbm);
        this.exp = exp;
    }

    private double roomError(Room r){
        ScanResult[] onlines = client.getFingerprints();
        if(onlines == null){
            return -1d;
        }
        double roomErr = 0; 

        for(ScanResult online : onlines){
            if(online.getSignal()>-80){
                ArrayList<ScanResult> offlines = r.getFingerprints(online.getBSSID());
                if(offlines != null){
                    double minOffline = Double.MIN_VALUE;
                    for(ScanResult offline : offlines){
                        if (minOffline < offline.getSignal())
                            minOffline = offline.getSignal();
                    }
                    roomErr += Math.pow(online.getSignal() - minOffline, 2);
                }   
            }
        }
        return Math.sqrt(roomErr);
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
            System.out.println("Error for: " + room.getId() + " = " + app);
            if(app == -1d) return null;
            if(min>app){
                min = app;
                roomId = room.getId();                
            }
        }
        return roomId;
    }

    private double normalize(double signalStrength){
        return (exp) ? exponentialRepresentation(signalStrength) : signalStrength;
    }
    
}
