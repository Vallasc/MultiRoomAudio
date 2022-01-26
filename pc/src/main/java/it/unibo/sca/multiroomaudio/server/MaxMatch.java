package it.unibo.sca.multiroomaudio.server;

import java.util.Arrays;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;

public class MaxMatch extends FingerprintAnalyzer{

    private final double ALPHA = 0.7;
    private final double BETA = 1d - ALPHA;

    public MaxMatch(SpeakerManager speakerManager, Client client, DatabaseManager dbm) {
        super(speakerManager, client, dbm);
    }

    @Override
    public String findRoomKey() {
        ScanResult[] online = client.getFingerprints();
        List<Room> offline = dbm.getClientRooms(client.getId());
        if(online == null ) return null;
        if(offline == null ) return null;
        Arrays.sort(online, ScanResult::compareByBSSID);
        
        String roomKey = null;
        double result = Double.MIN_VALUE;
        for(Room r : offline){
            int nHit = 0;
            int nMiss = 0;
            String[] keys = r.getBSSID();
            //maybe find a way to sort the keys in the offline phase already
            /*if there's something that the server sends to the client to tell it to stop the scan process 
            for the entire room then the same thing could trigger the ordering of the keys
            new field in room:
                String orderedKeys[];
                orderedKeys = new String[keyset().size()];
                Array.sort(orderedKeys, String::compareTo);

            and then in here String[] keys = r.getBSSID() is already ordered;*/
            Arrays.sort(keys, String::compareTo);
            //lists are sorted, use the sorting now
            int i = 0, j = 0;//i is for online, j is for offline
            while(i < online.length && j < keys.length){
                //0 if equal, < 0 if online < offline, > 0 if online > offline
                int comp = online[i].getBSSID().compareTo(keys[j]);
                if(comp == 0){
                    nHit += 1;
                    i += 1;
                    j += 1;
                }else if(comp < 0){
                    i += 1;
                }else if(comp > 0){
                    nMiss += 1;
                    j += 1;
                }
            }
            //here is checked if one between i and j hasn't reached the end of their rispective arrays 
            //it means that there are more non matching AP
            nMiss += (online.length - i) + (keys.length - j);
            double tmp = ((ALPHA * nHit) - (BETA * nMiss))/(keys.length);
            if(tmp > result){
                result = tmp;
                roomKey = r.getId();
            }
            /*System.out.println(r.getId());
            System.out.println("\t online: " + online.length + " " + i + "\t offline: " + keys.length + " " +  j);
            System.out.println( "\t match: " + nHit + "\t non match: " + nMiss + "\n\tfinal evaulation: " + tmp);*/
        }
        return roomKey;
    }
    
}
