package it.unibo.sca.multiroomaudio.server;

import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MaxMatch extends FingerprintAnalyzer{

    public MaxMatch(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm);
    }

    @Override
    public String findRoomKey() {
        ScanResult[] online = client.getFingerprints();
        List<Room> offline = dbm.getClientRooms(client.getId());
        int max = 0;
        String roomKey = null;
        if(online == null ) return null;
        if(offline == null ) return null;
        for(Room r : offline){
            int tmp = 0;
            String[] keys = r.getBSSID();
            //this could be done better if we sort the two lists maybe, need to see how < and > work on string (if it works at all)
            //lists are sorted, use the sorting now
            for(String key : keys){
                for(ScanResult ap : online){
                    if(ap.getBSSID().equals(key))
                        tmp++;
                }
            }
            if(tmp >= max){
                max = tmp;
                roomKey = r.getId();
            }
            System.out.println(r.getId() + " matches: " + tmp);
        }
        return roomKey;
    }
    
}
