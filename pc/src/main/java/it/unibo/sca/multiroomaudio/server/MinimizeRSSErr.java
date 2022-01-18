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

    private double roomError(String[] apId, Room r){
        //max 4 reference points for each room
        ScanResult[] onlines = client.getFingerprints();
        int nscan = r.getNScan();
        double[] roomErr = new double[nscan]; 
        Arrays.fill(roomErr, 0);
        for(ScanResult online : onlines){
            //mean for the ReferencePoint_rpIndex in the room 
            ScanResult[] offlines = r.getFingerprints(online.getBSSID());
            //i'm not sure to have all the AP for each scan
            if(offlines != null)
                for(int rpIndex = 0; rpIndex < nscan; rpIndex++){
                    if(offlines.length > rpIndex){   
                        ScanResult offline = offlines[rpIndex];
                        roomErr[rpIndex] += Math.pow(normalize(online.getSignal()) - normalize(offline.getSignal()), 2);
                    }else if(offlines.length <= rpIndex){
                        roomErr[rpIndex] += Math.pow(normalize(online.getSignal()) - normalize(MIN_STRENGTH), 2);
                    }
                }
            else
                for(int rpIndex = 0; rpIndex < nscan; rpIndex++){
                    roomErr[rpIndex] += Math.pow(normalize(online.getSignal()) - normalize(MIN_STRENGTH), 2);
                }
        }
        return 0d;
    }

    @Override
    public String findRoomKey() {
        List<Room> rooms = dbm.getClientRooms(client.getId());
        for(Room r:rooms){
            //ordered
            String[] apId = r.getBSSID();
            roomError(apId, r);
        }    
        return null;
    }

    private double normalize(double signalStrength){
        double ret = positiveRepresentation(signalStrength);
        return (exp) ? exponentialRepresentation(ret) : ret;
    }
    
}
