package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;
import it.unibo.sca.multiroomaudio.utils.Utils;

public abstract class FingerprintAnalyzer extends Thread {

    public static final int SLEEP_TIME = 200;
    protected final Client client;
    protected final DatabaseManager dbm;
    private final SpeakerManager speakerManager;
    private boolean stopped;

    public FingerprintAnalyzer(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
        this.speakerManager = speakerManager;
        this.stopped = false;
    }

    abstract public String findRoomKey();
    abstract public void printResults();

    @Override
    public void run(){
        String prevRoomKey = "";
        List<Speaker> prevSpeakers = new ArrayList<Speaker>();
        System.out.println("START FINGERPRINT ANALYZER: " + client.getId());

        while(!this.stopped){
            String roomkey = findRoomKey();
            if(roomkey != null){
                List<Speaker> speakers = dbm.getConnectedSpeakerRoom(client.getId(), roomkey);
                boolean areSameSpeakers = speakers.containsAll(prevSpeakers) && prevSpeakers.containsAll(speakers);
                if( !roomkey.equals(prevRoomKey) || !areSameSpeakers ) { // new Room or new Speaker
                    System.out.println("DEBUG room is: " + roomkey);
                    this.printResults();
                    prevSpeakers.forEach((s) -> s.decNumberNowPlaying());
                    prevSpeakers.clear();
                    prevSpeakers.addAll(speakers);
                    prevSpeakers.forEach((s) -> s.incNumberNowPlaying());
                    speakerManager.updateAudioState();
                    speakerManager.updateSpeakerList();

                    if(!roomkey.equals(prevRoomKey)) // Do room urls requests
                        doRoomRequests(prevRoomKey, roomkey);
                }
                prevRoomKey = roomkey;
            }
            this.printResults();
            Utils.sleep(SLEEP_TIME);
        }
        prevSpeakers.forEach((s) -> s.decNumberNowPlaying());
        prevSpeakers.clear();
        speakerManager.updateAudioState();
        speakerManager.updateSpeakerList();
        doRoomRequests(prevRoomKey, null);
        System.out.println("STOP FINGERPRINT ANALYZER: " + client.getId());
    }

    public void stopService() {
        this.stopped = true;
    }

    public void doRoomRequests(String prevRoomKey, String newRoomKey){
        if( prevRoomKey != null ){
            Room prevRoom = dbm.getClientRoom(client.getId(), prevRoomKey);
            if(prevRoom != null && !prevRoom.getUrlLeave().equals("")){
                Utils.getHTTPRequest(prevRoom.getUrlLeave());
            }
        }
        if( newRoomKey != null ){
            Room newRoom = dbm.getClientRoom(client.getId(), newRoomKey);
            if(newRoom != null && !newRoom.getUrlEnter().equals("")){
                Utils.getHTTPRequest(newRoom.getUrlEnter());
            }
        }
    }

}
