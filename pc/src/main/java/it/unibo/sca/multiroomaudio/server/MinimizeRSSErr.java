package it.unibo.sca.multiroomaudio.server;

import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MinimizeRSSErr extends FingerprintAnalyzer{
    private boolean exp;
    public MinimizeRSSErr(Client client, DatabaseManager dbm, boolean exp) {
        super(client, dbm);
        this.exp = exp;
    }

    private double roomError(Room r){
        //max 4 reference points for each room
        ScanResult[] onlines = client.getFingerprints();
        int nscan = r.getNScan();
        double[] roomErr = new double[nscan]; 
        Arrays.fill(roomErr, 0);
        for(ScanResult online : onlines){
            //mean for the ReferencePoint_rpIndex in the room 
            ScanResult[] offlines = r.getFingerprints(online.getBSSID());
            //i'm not sure to have all the AP for each scan so that's what happens
            if(offlines != null)
                for(int rpIndex = 0; rpIndex < nscan; rpIndex++){
                    if(offlines.length > rpIndex){   
                        //if the lenght of the offline list is > than the index it means we are good and we can just compute the error ezpz
                        //if there are value missing in the positions before the current one then it's fixed inside the putFingerprints
                        ScanResult offline = offlines[rpIndex];
                        roomErr[rpIndex] += Math.pow(normalize(online.getSignal()) - normalize(offline.getSignal()), 2);
                    }else if(offlines.length <= rpIndex){
                        //otherwise the value is missing, again, confront with the lowest value
                        roomErr[rpIndex] += Math.pow(normalize(online.getSignal()) - normalize(MIN_STRENGTH), 2);
                    }
                }
            else
                //if there are no offline fingerprints for that ap then its error is given a huge value
                //other option is to not consider the thing at all
                for(int rpIndex = 0; rpIndex < nscan; rpIndex++){
                    roomErr[rpIndex] += Math.pow(normalize(online.getSignal()) - normalize(MIN_STRENGTH), 2);
                }
        }
        double min = Math.sqrt(roomErr[0]);
        for(int i = 1; i<roomErr.length; i++){
            double root = Math.sqrt(roomErr[i]);
            if(min>root)
            //getting the rp that minimizes the error for the room
                min = root;
        }

        return min;
    }

    @Override
    public String findRoomKey() {
        List<Room> rooms = dbm.getClientRooms(client.getId());
        if(rooms == null) return null;
        if(rooms.size()<=0) return null;
        Room room = rooms.get(0);
        String roomId = room.getId();
        double min = roomError(room);
        for(int i = 1; i<rooms.size(); i++){
            room = rooms.get(i);
            double app = roomError(room);
            if(min>app){
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
