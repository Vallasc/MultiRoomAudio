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
            if(tmp > max){
                max = tmp;
                roomKey = r.getId();
            }
        }
        return roomKey;
    }

    @Override
    public void run(){
        String speakerId = ""; //comes from the distance algorithm
        String oldSpeakerId = null;
        String newSpeakerId = null;
        Speaker oldSpeaker = null;
        while(client.getPlay()){
            //do whatever computation u need
            //distance = computeDistance();
            speakerId = MaxMatch();
            if(speakerId == null)
                continue;

            if(oldSpeakerId == null){
                try{
                    oldSpeaker = (Speaker)dbm.getDevice(speakerId);
                }
                catch(ClassCastException e){
                    System.err.println("Casting something that's not a speaker (oldspeaker)");
                }
                oldSpeakerId = oldSpeaker.getId();
                oldSpeaker.setMuted(false);
                oldSpeaker.incNumberNowPlaying();
            }else{
                //should create a new speaker and break the reference that's created below*
                Speaker newSpeaker = null;
                try{
                    oldSpeaker = (Speaker)dbm.getDevice(oldSpeakerId);
                    newSpeaker = (Speaker)dbm.getDevice(speakerId);
                }catch(ClassCastException e){
                    System.err.println("Casting something that's not a speaker (newspeaker)");
                }
                newSpeakerId = newSpeaker.getId();
                //changed room
                if(!oldSpeakerId.equals(newSpeakerId)){
                    newSpeaker.setMuted(false);
                    //dec and if n is 0 mutes the speaker
                    oldSpeaker.decNumberNowPlaying();
                    newSpeaker.incNumberNowPlaying();
                    //*reference
                    oldSpeakerId = newSpeakerId;
                    newSpeakerId = null;
                }
            }
            
        }
    }
    
}
