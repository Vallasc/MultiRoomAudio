package it.unibo.sca.multiroomaudio.server;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public abstract class FingerprintAnalyzer extends Thread {
    private static final double ALPHA = 24;
    protected static final int MIN_STRENGTH = -80;
    protected static final int MAX_VALUE = 15000;

    protected final Client client;
    protected final DatabaseManager dbm;
    private SpeakerManager speakerManager;
    private boolean stopped;

    public FingerprintAnalyzer(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
        this.speakerManager = speakerManager;
        // TODO non si potrebbbe mettere che manda il messaggio solo quando cambia la stanza?
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                speakerManager.updateAudioState();
            }
        },0,2000);
        this.stopped = false;
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
        String prevRoomKey = null;
        System.out.println("START FINGERPRINT ANALYZER: " + client.getId());
        while(!this.stopped){
            String roomkey = findRoomKey();
            if(roomkey == null){
                continue;
            }
            List<Speaker> speakers = dbm.getConnectedSpeakerRoom(client.getId(), roomkey);
            if(speakers == null || speakers.size() == 0){
                continue;
            }
            if(prevRoomKey == null){
                //System.out.println("PrevRoomKey is null, ROOMKEY: " + roomkey);
                speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                prevRoomKey = roomkey;
            }
            if(!prevRoomKey.equals(roomkey)){
               // System.out.println("PrevRoomKey is not null, ROOMKEY: " + roomkey + " PREV: " + prevRoomKey);
                List<Speaker> prevspeakers = dbm.getConnectedSpeakerRoom(client.getId(), prevRoomKey);
                if(prevspeakers != null)
                    prevspeakers.forEach(speaker -> speaker.decNumberNowPlaying());
                speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                prevRoomKey = roomkey;

            }
        }
        System.out.println("STOP FINGERPRINT ANALYZER: " + client.getId());
    }


    public void stopService() {
        this.stopped = true;
    }
    
}
