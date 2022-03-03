package it.unibo.sca.multiroomaudio.server.localization_algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.FingerprintAnalyzer;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class Bayes extends FingerprintAnalyzer{

    public Bayes(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm);
    }

    private double compute(double x, double mu, double stddev){
        double exponent = Math.pow(Math.E, -Math.pow((x-mu), 2)/2*Math.pow(stddev, 2));
        return (1/Math.sqrt(2*Math.PI*stddev))*exponent;
    }
    
    private double roomError(Room r, ScanResult[] onlines){
        if(onlines == null){
            return -1d;
        }
        int nScan = r.getNScan();
        if(nScan == 0){
            return -1d;
        }
        
        double[] prob = new double[nScan];

        int initIndex = 0; 
        ScanResult online;
        ArrayList<ScanResult> offlines = null;
        boolean flagFound = false;
        while(initIndex < onlines.length && !flagFound){
            online = onlines[initIndex];
            offlines = r.getFingerprints(online.getBSSID());
            if(offlines != null){
                for(int j = 0; j < offlines.size(); j++){
                    prob[j] = compute(online.getSignal(), offlines.get(j).getSignal(), offlines.get(j).getStddev());
                }
                flagFound = true;
            }
            initIndex +=1;
            
        }
        

        for(int i = initIndex; i<onlines.length; i++){
            online = onlines[i];
            offlines = r.getFingerprints(online.getBSSID());
            if(offlines != null){
                for(int j = 0; j < offlines.size(); j++){
                    prob[j] *= compute(online.getSignal(), offlines.get(j).getSignal(), offlines.get(j).getStddev());
                }
            }
        }

        Arrays.sort(prob);
        System.out.println(r.getId());
        for(int i = 0; i<prob.length; i++){
            System.out.println(prob[i]);
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

        return prob[prob.length - 1];
    }

    @Override
    public String findRoomKey() {

        List<Room> rooms = dbm.getClientRooms(client.getId());
        if(rooms == null){
            return null;
        } 
        if(rooms.size()<=0) {
            return null;
        }
        String roomId = null;
        ScanResult[] onlines = client.getFingerprints();

        double max = -1;
        
        for(Room room : rooms){
            double app = roomError(room, onlines);
            if(app == -1d) return null;
            if(app > max){
                max = app;
                roomId = room.getId();
            }
            
        }   
        return roomId;
    }   
    
}
