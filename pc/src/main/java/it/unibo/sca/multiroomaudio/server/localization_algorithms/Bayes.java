package it.unibo.sca.multiroomaudio.server.localization_algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    private double compute(double x, double mu, double variance){
        double den = Math.sqrt(variance*2*Math.PI);
        return (1/den) *  
                    Math.pow(Math.E, 
                        (-Math.pow(x - mu, 2) /
                        (2*variance))
                );
    }

    //maybe i should aggregate the different ap for each rp and these are the values of mean and variance
    private List<Double> aggregateMean(Room r, HashMap<String, List<ScanResult>> fingerprints){
        List<Double> mean = new ArrayList<>(r.getNScan());
        for(int i = 0; i < r.getNScan(); i++){
            mean.add(i, 0d);
        }

        for(String key : fingerprints.keySet()){
            ArrayList<ScanResult> offlines = (ArrayList<ScanResult>) fingerprints.get(key);
            for(int i = 0; i < offlines.size(); i++){
                mean.set(i, mean.get(i) + offlines.get(i).getSignal());
            }
        }
        for(int i = 0; i < mean.size(); i++){
            mean.set(i, mean.get(i)/fingerprints.size());
        }
        return mean;
    }

    private List<Double> aggregateVariance(Room r, List<Double> mean, HashMap<String, List<ScanResult>> fingerprints){
        List<Double> variance = new ArrayList<>(r.getNScan());
        for(int i = 0; i < r.getNScan(); i++){
            variance.add(i, 0d);
        }

        for(String key : fingerprints.keySet()){
            ArrayList<ScanResult> offlines = (ArrayList<ScanResult>) fingerprints.get(key);
            for(int i = 0; i < offlines.size(); i++){
                variance.set(i, variance.get(i) + Math.pow(offlines.get(i).getSignal() - mean.get(i), 2));
            }
        }
        for(int i = 0; i < variance.size(); i++){
            variance.set(i,  variance.get(i)/(fingerprints.size()-1));
        }
        return variance;
    }

    private double roomError(Room r, ScanResult[] onlines){
        
        if(onlines == null){
            return -1d;
        }
        
        HashMap<String, List<ScanResult>> fingerprints = r.getFingerprints();
        List<Double> means = aggregateMean(r, fingerprints);
        List<Double> variance = aggregateVariance(r, means, fingerprints);
        
        /*System.out.println("means : ");
        for(double mean: means){
            System.out.println("\t" + mean);
        }

        System.out.println("variance : ");
        for(double var: variance){
            System.out.println("\t" + var);
        }*/

        double[] prob = new double[r.getNScan()];
        Arrays.fill(prob, 1);

        for(ScanResult online : onlines){
            for(int i = 0; i < r.getNScan(); i++){
                prob[i] *=  compute(online.getSignal(), means.get(i), variance.get(i));
            }
        }
        //System.out.println("Probabilities for " + r.getId() + ": ");
        for(int i = 0; i < prob.length; i++){
            System.out.println("\t" + prob[i]);
        }
        Arrays.sort(prob);

        return prob[prob.length - 1];
    }

    @Override
    public String findRoomKey() {

        List<Room> rooms = dbm.getClientRooms(client.getId());
        if(rooms == null){
            //System.out.println("rooms is null");
            return null;
        } 
        if(rooms.size()<=0) {
            //System.out.println("rooms is empty");
            return null;
        }
        String roomId = null;
        ScanResult[] onlines = client.getFingerprints();

        Room room = rooms.get(0);
        double max = roomError(room, onlines);
        if(max == -1d) return null;
        roomId = room.getId();
        for(int i = 1; i<rooms.size(); i++){
            room = rooms.get(i);
            double app = roomError(room, onlines);
            if(app == -1d) return null;
            if(app > max){
                max = app;
                roomId = room.getId();
            }
        }   
        //System.out.println("Room: " + roomId);
        return roomId;
    }   
    
}
