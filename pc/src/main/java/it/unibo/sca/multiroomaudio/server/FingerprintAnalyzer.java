package it.unibo.sca.multiroomaudio.server;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public class FingerprintAnalyzer implements Runnable{
    
    private final Client client;
    private final DatabaseManager dbm;

    public FingerprintAnalyzer(Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
    }

    @Override
    public void run(){
        String speakerId = "";
        String oldSpeakerId = null;
        String newSpeakerId = null;
        Speaker oldSpeaker = null;
        while(client.getPlay()){
            //do whatever computation u need
            //distance = computeDistance();
            //speakerId = selectSpeakerId(distance);
            
                if(oldSpeakerId == null){
                    try{
                        oldSpeaker = (Speaker)dbm.getDevice("speakerId");
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
                        newSpeaker = (Speaker)dbm.getDevice("speakerId");
                    }catch(ClassCastException e){
                        System.err.println("Casting something that's not a speaker (newspeaker)");
                    }
                    newSpeakerId = newSpeaker.getId();
                    //changed room
                    if(!oldSpeakerId.equals(newSpeakerId)){
                        newSpeaker.setMuted(false);
                        oldSpeaker.decNumberNowPlaying();
                        newSpeaker.incNumberNowPlaying();
                        //*reference
                        oldSpeaker = newSpeaker;
                        oldSpeakerId = newSpeakerId;
                        newSpeakerId = null;
                    }
                }
            
        }
    }
    
}
