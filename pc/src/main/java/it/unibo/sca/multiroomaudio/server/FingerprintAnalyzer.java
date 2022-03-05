package it.unibo.sca.multiroomaudio.server;

import java.util.HashMap;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;
import it.unibo.sca.multiroomaudio.utils.Utils;

public abstract class FingerprintAnalyzer extends Thread {
    public static class Printer{
        HashMap<String, double[]> res;
        HashMap<String, Integer> knnRes;
        int type;
        public Printer(int type){
            this.type = type;
            if(this.type == 0)
                this.res = new HashMap<>();
            if(this.type == 1)
                this.knnRes = new HashMap<>();

        }

        public void setKnn(HashMap<String, Integer> knnRes){
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
                for(String s : this.knnRes.keySet()){
                    System.out.println("Room: " + s + " corrispondences: " + knnRes.get(s));
                }
            }
        }
    }

    private static final double ALPHA = 24;
    protected static final int MIN_STRENGTH = -80;
    protected static final int MAX_VALUE = 15000;
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
        int sLen = 0;
        while(!this.stopped){
            String roomkey = findRoomKey();
            if(roomkey != null){
                List<Speaker> speakers = dbm.getConnectedSpeakerRoom(client.getId(), roomkey);
                if(sLen != speakers.size()){
                    if(prevRoomKey != null && prevRoomKey.equals(roomkey)){
                        System.out.println("DEBUG room was: " + roomkey + ", resetting cause new speaker connected");
                        this.printer.print();        
                        try{ 
                            speakers.forEach(speaker -> speaker.decNumberNowPlaying());
                        }catch(NullPointerException e){
                            System.err.println("null pointer when updating speakers");
                        }               
                        prevRoomKey = null;
                    }        
                    sLen = speakers.size();
                }
                if(prevRoomKey == null){
                    System.out.println("DEBUG room is: " + roomkey);
                    this.printer.print();        
                    try{ 
                        speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                    }catch(NullPointerException e){
                        System.err.println("null pointer when updating speakers");
                    }
                    speakerManager.updateAudioState();
                    speakerManager.updateSpeakerList();
                    prevRoomKey = roomkey;
                }
                else if(!prevRoomKey.equals(roomkey)){
                    System.out.println("DEBUG room is: " + roomkey);
                    this.printer.print();        
                    List<Speaker> prevspeakers = dbm.getConnectedSpeakerRoom(client.getId(), prevRoomKey);
                    try{
                        prevspeakers.forEach(speaker -> speaker.decNumberNowPlaying());
                        speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                    }catch(NullPointerException e){
                        System.err.println("null pointer when updating speakers");
                    }
                    speakerManager.updateAudioState();
                    prevRoomKey = roomkey;
                }
            }
            Utils.sleep(SLEEP_TIME);
        }
        System.out.println("STOP FINGERPRINT ANALYZER: " + client.getId());
    }

    public void stopService() {
        this.stopped = true;
    }
    
}
