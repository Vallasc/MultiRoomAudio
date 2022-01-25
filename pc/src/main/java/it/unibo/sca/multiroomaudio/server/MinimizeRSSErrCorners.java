package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MinimizeRSSErrCorners extends FingerprintAnalyzer{

    public MinimizeRSSErrCorners(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm);
    }

    private double roomError(Room r){
        ScanResult[] onlines = client.getFingerprints();
        if(onlines == null){
            return -1d;
        }

        List<Double> roomErr = new ArrayList<>(); 

        int i = 0;
        ScanResult online = onlines[i];
        if(online.getSignal() > -80){
            ArrayList<ScanResult> offlines = r.getFingerprints(online.getBSSID());
            if(offlines != null)
                for(ScanResult offline : offlines)
                    roomErr.add(Math.pow(online.getSignal() - offline.getSignal(), 2));
        }
        i += 1; 
        while(i < onlines.length){ 
            if(online.getSignal()>-80){
                ArrayList<ScanResult> offlines = r.getFingerprints(online.getBSSID());
                if(offlines != null){
                    //for each corner if i'm not wrong
                    for(int j = 0; j < offlines.size(); j++){
                        if(j<roomErr.size())
                            roomErr.add(j, roomErr.get(j)+(Math.pow(online.getSignal() - offlines.get(j).getSignal(), 2)));
                        else
                            roomErr.add((Math.pow(online.getSignal() - offlines.get(j).getSignal(), 2)));
                    }
                }
            }   
            i += 1; 
        }
        roomErr.stream().forEach(Math::sqrt);
        roomErr.sort(Double::compareTo);
        System.out.println(r.getId() + "min: " + roomErr.get(0));
        return roomErr.get(0);
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
                    min = app;
                    roomId = room.getId();   
                }
        }
        return roomId;
    }   
}
