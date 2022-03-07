package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;
import it.unibo.sca.multiroomaudio.utils.Utils;

public abstract class FingerprintAnalyzer extends Thread {

    private static final int SLEEP_TIME = 200;
    protected final Client client;
    protected final DatabaseManager dbm;

    protected double[] roomErr;
    private SpeakerManager speakerManager;
    private boolean stopped;
    protected int type;
    protected Printer printer;
    

    public FingerprintAnalyzer(SpeakerManager speakerManager, Client client, DatabaseManager dbm, int type) {
        this.client = client;
        this.dbm = dbm;
        this.speakerManager = speakerManager;
        this.stopped = false;
        this.printer = new Printer(type);
        this.type = type;
    }


    abstract public String findRoomKey();

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
                    this.printer.print();
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
            //this.printer.print();
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
    
    public static class Printer{
        HashMap<String, double[]> res;
        HashMap<String, Double> knnRes;
        int type;
        public Printer(int type){
            this.type = type;
            if(this.type == 0)
                this.res = new HashMap<>();
            if(this.type == 1)
                this.knnRes = new HashMap<>();

        }

        public void setKnn(HashMap<String, Double> knnRes){
            this.knnRes = knnRes;
        }


        public void set(String s, double[] res){
            this.res.put(s, res);
        }

        public void print(){
            if(this.type == 0){
                double[] arr;
                for(String key : this.res.keySet()){
                    System.out.println("Results for room: " + key);
                    arr = this.res.get(key);
                    for(int i = 0; i < arr.length; i++)
                        System.out.println(arr[i]);
                }
            }
            if(this.type == 1){
                System.out.println("K classes");
                for(String s : this.knnRes.keySet()){
                    System.out.println("Room: " + s + " corrispondences: " + knnRes.get(s));
                }
                System.out.println("");
            }
        }
    }

}
