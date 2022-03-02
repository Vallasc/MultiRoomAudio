package it.unibo.sca.multiroomaudio.server;

import java.util.HashMap;
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
    protected HashMap<String, double[]> result = new HashMap<String, double[]>();
    private SpeakerManager speakerManager;
    private boolean stopped;

    public FingerprintAnalyzer(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        this.client = client;
        this.dbm = dbm;
        this.speakerManager = speakerManager;
        this.stopped = false;
    }

    protected static double positiveRepresentation(double inputSignal){
        return inputSignal - MIN_STRENGTH;
    }

    protected static double exponentialRepresentation(double inputSignal){
        return Math.exp(inputSignal/ALPHA) / Math.exp(-MIN_STRENGTH/ALPHA);
    }

    protected void printResult(String s){
        if(this.result.isEmpty()) return;
        System.out.println("Printing from " + s + " code");
        for(String key : result.keySet()){
            System.out.println("room: " + key);
            double[] arr = result.get(key);
            for(int i = 0; i < arr.length; i++){
                System.out.println("\t" + arr[i]);
            }
        }
    }

    protected void setResult(String k, double[] arr){
        result.put(k, arr);
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
            
            if(prevRoomKey == null && roomkey != null){
                if(speakers != null && speakers.size() != 0){ 
                    speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                    speakerManager.updateAudioState();
                }
                prevRoomKey = roomkey;
                this.printResult("prevRoomKey null");
                continue;
            }
            if(!prevRoomKey.equals(roomkey)){
                List<Speaker> prevspeakers = dbm.getConnectedSpeakerRoom(client.getId(), prevRoomKey);
                if(prevspeakers != null)
                    prevspeakers.forEach(speaker -> speaker.decNumberNowPlaying());
                if(speakers != null && speakers.size() != 0){ 
                    speakers.forEach(speaker -> speaker.incNumberNowPlaying());
                }
                speakerManager.updateAudioState();
                prevRoomKey = roomkey;
                this.printResult("prevRoomKey exist");
            }
        }
        System.out.println("STOP FINGERPRINT ANALYZER: " + client.getId());
    }


    public void stopService() {
        this.stopped = true;
    }
    
    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
