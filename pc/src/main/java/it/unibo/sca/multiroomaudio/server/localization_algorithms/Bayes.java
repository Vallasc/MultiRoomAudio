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
        double numExp = Math.pow((x-mu), 2);
        double denExp = 2*Math.pow(stddev, 2);
        double exponent = Math.pow(2.718, -(numExp/denExp));
        return (1/(Math.sqrt(2*Math.PI)*stddev))*exponent;
    }
    
    private double roomError(Room r, List<ScanResult> onlines, int rSize){
        if(onlines == null){
            return -1d;
        }
        int nScan = r.getNScan();
        if(nScan == 0){
            return -1d;
        }

        List<String> notFound = new ArrayList<>(r.getFingerprints().keySet());
        double[] prob = new double[nScan];
        Arrays.fill(prob, 0);
        for(ScanResult online : onlines){
            notFound.remove(online.getBSSID());
            List<ScanResult> offlines = r.getFingerprints(online.getBSSID());
            if(offlines != null){
                for(int i = 0; i < offlines.size(); i++){
                    if(offlines.get(i) != null){
                        if(prob[i] == 0)
                            prob[i] = compute(online.getSignal(), offlines.get(i).getSignal(), offlines.get(i).getStddev());
                        else
                            prob[i] *= compute(online.getSignal(), offlines.get(i).getSignal(), offlines.get(i).getStddev());
                    }
                }
            }
        }

        for(String BSSID : notFound){
            List<ScanResult> offlines = r.getFingerprints(BSSID);
            if(offlines != null){
                for(int i=0; i< offlines.size(); i++){
                    if(offlines.get(i) != null)
                        prob[i] *= 1/rSize;
                }
            }
        }

        Arrays.sort(prob);
        //super.printer.set(r.getId(), prob);

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
        List<ScanResult> onlines = client.getFingerprints();

        double max = -1;
        
        for(Room room : rooms){
            double app = roomError(room, onlines, rooms.size());
            if(app == -1d) return null;
            if(app > max){
                max = app;
                roomId = room.getId();
            }
            
        }   
        return roomId;
    }

    @Override
    public void printResults() {
        // TODO Auto-generated method stub
        
    }   
    
}
