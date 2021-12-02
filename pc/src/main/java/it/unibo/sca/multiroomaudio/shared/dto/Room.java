package it.unibo.sca.multiroomaudio.shared.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

//Stanza(Nome, Map<idClient, List<Fingerprint>>)
public class Room {
    String id;
    List<String> activeClients = new ArrayList<>();//i need this to know which clients are scanning in which room during the offline phase
    final ConcurrentHashMap<String, List<Fingerprint>> clientFingerprints;
    
    public Room(String id, ConcurrentHashMap<String, List<Fingerprint>> clientFingerprints){
        this.id = id;
        this.clientFingerprints = clientFingerprints;
    }

    public void setNewClient(String clientId){
        clientFingerprints.put(clientId, new ArrayList<Fingerprint>());
    }
    
    public String getId(){
        return id;
    }

    public void putActive(String clientId){
        activeClients.add(clientId);
    }

    //synchronized just to be sure evenif only one thread is supposed to remove a client
    public synchronized void removeActive(String clientId){
        activeClients.remove(clientId);
    }

    public synchronized void putClientFingerprints(String clientId, List<Fingerprint> fingerprints){
        clientFingerprints.get(clientId).addAll(fingerprints);
        //server tenerli ordinati?
    }
}
