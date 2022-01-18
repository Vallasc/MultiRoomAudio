package it.unibo.sca.multiroomaudio.server;

import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public abstract class FingerprintAnalyzer implements Runnable{
    
    protected final Client client;
    protected final DatabaseManager dbm;
    private static final double ALPHA = 24;
    protected static final int MIN_STRENGTH = -120;

    public FingerprintAnalyzer(Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
    }

    protected static double positiveRepresentation(double inputSignal){
        return inputSignal - MIN_STRENGTH;
    }

    protected static double exponentialRepresentation(double inputSignal){
        return Math.exp(inputSignal/ALPHA) / Math.exp(-MIN_STRENGTH/ALPHA);
    }

    abstract public String findRoomKey();//in case of max n returns max, in case of euclidean returns the min error for each ap

    @Override
    public void run(){
        while(client.getPlay()){
            String prevRoomKey = null;
            String roomkey = findRoomKey();
            System.out.println("ONLINE roomkey: " + roomkey);
            if(roomkey == null)
                continue;
            //unmute the room 
            if(prevRoomKey == null){
                List<Speaker> speakers = dbm.getConnectedSpeakerRoom(roomkey);
                speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                prevRoomKey = roomkey;
            }
            if(!prevRoomKey.equals(roomkey)){
                List<Speaker> prevspeakers = dbm.getConnectedSpeakerRoom(prevRoomKey);
                List<Speaker> speakers = dbm.getConnectedSpeakerRoom(roomkey);
                speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                prevspeakers.forEach(speaker -> speaker.decNumberNowPlaying());
                prevRoomKey = roomkey;
            }
        }
    }
    
}
