package it.unibo.sca.multiroomaudio.server;

import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public abstract class FingerprintAnalyzer implements Runnable{
    private static final double ALPHA = 24;
    protected static final int MIN_STRENGTH = -80;
    protected static final int MAX_VALUE = 15000;

    protected final Client client;
    protected final DatabaseManager dbm;
    protected SpeakerManager speakerManager;

    public FingerprintAnalyzer(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
        this.speakerManager = speakerManager;
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
        client.setPlay(true);
        String prevRoomKey = null;
        while(client.getPlay()){
            String roomkey = findRoomKey();
            if(roomkey == null){
                continue;
            }
            //System.out.println("Roomkey: " + roomkey);
            if(prevRoomKey == null){
                //System.out.println("PrevRoomKey is null, ROOMKEY: " + roomkey);
                List<Speaker> speakers = dbm.getConnectedSpeakerRoom(client.getId(), roomkey);
                if(speakers != null)
                    speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                prevRoomKey = roomkey;
            }
            if(!prevRoomKey.equals(roomkey)){
               // System.out.println("PrevRoomKey is not null, ROOMKEY: " + roomkey + " PREV: " + prevRoomKey);
                List<Speaker> prevspeakers = dbm.getConnectedSpeakerRoom(client.getId(), prevRoomKey);
                List<Speaker> speakers = dbm.getConnectedSpeakerRoom(client.getId(), roomkey);
                if(prevspeakers != null)
                    prevspeakers.forEach(speaker -> speaker.decNumberNowPlaying());
                if(speakers != null)
                    speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                prevRoomKey = roomkey;
            }
            speakerManager.updateAudioState();
        }
    }
    
}
