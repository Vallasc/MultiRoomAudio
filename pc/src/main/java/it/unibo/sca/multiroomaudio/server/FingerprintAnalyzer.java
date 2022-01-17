package it.unibo.sca.multiroomaudio.server;

import java.util.List;

import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public class FingerprintAnalyzer implements Runnable{
    
    private final Client client;
    private final DatabaseManager dbm;

    public FingerprintAnalyzer(Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
    }

    private String MaxMatch(){
        APInfo[] online = client.getFingerprints();
        List<Room> offline = dbm.getClientRooms(client.getId());
        int max = 0;
        String roomKey = null;
        for(Room r : offline){
            int tmp = 0;
            String[] keys = r.getBSSID();
            //this could be done better if we sort the two lists maybe, need to see how < and > work on string (if it works at all)
            for(String key : keys){
                for(APInfo ap : online){
                    if(ap.getBSSID().equals(key))
                        tmp++;
                }
            }
            if(tmp >= max){
                max = tmp;
                roomKey = r.getId();
            }
        }
        return roomKey;
    }

    @Override
    public void run(){
        while(client.getPlay()){
            //do whatever computation u need
            //distance = computeDistance();
            String roomkey = MaxMatch();
            if(roomkey == null)
                continue;

            List<Speaker> speakers = dbm.getConnectedSpeakerRoom(roomkey);
            speakers.forEach(speaker -> speaker.setMuted(false));
        }
    }
    
}
