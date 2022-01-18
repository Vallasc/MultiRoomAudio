package it.unibo.sca.multiroomaudio.server;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;
import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
/*HashMap<id, Device> (tutti i device)
list<device> (device connessi)
Map<clientID, stanza> -> Stanza(Nome, Map<idClient, List<Fingerprint>>)  -> 
Device -> se il device è un client il fingerprint va salvato dentro device e il thread che si occupa del calcolo gira sui device connessi
        che sono client
        alcune robe che vengono accedute da device devono essere accedute con metodi synchronized
MusicOrchestrationManager -> (list<speaker>, minutaggio, canzone)*/
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.model.*;

public class DatabaseManager {
    private final List<Song> songs = new ArrayList<Song>();

    private final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server

    //private final ConcurrentHashMap<String, Pair<Session, Device>> connectedWebDevices = new ConcurrentHashMap<>(); //all the connected web devices
    private final ConcurrentHashMap<Session, Device> connectedWebDevices = new ConcurrentHashMap<>(); //should be to save the sessions 

    private final ConcurrentHashMap<String, Client> connectedSocketDevices = new ConcurrentHashMap<>(); //all the connected socket devices
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Room>> clientScans = new ConcurrentHashMap<>(); //the rooms for each client

    //-------------------------------DEVICES-----------------------------------------------------------

    public String getKeyDevice(String id){
        // Can throw ConcurrentModificationException if an element is deleted during the iteration
        // but we don't delete devices :)
        for(String key : devices.keySet())
            if(devices.get(key).getId().equals(id))
                return key;
        return null;
    }

    public boolean deviceContains(String device){
        return devices.containsKey(device);
    }

    public Device getDevice(String key){
        return devices.get(key);
    }

    public boolean setDeviceStart(String clientId, String roomId, int nScan){
        setDeviceStop(clientId, nScan);
        try{
            ((Client) devices.get(clientId)).setStart(true, roomId, nScan);
            return true;
        }catch(ClassCastException e){
            System.err.println("you casted a speaker to a client, what's going on?");
            return false;
        }
    }

    public boolean setDeviceStop(String clientId, int nscan){
        try{
            ((Client) devices.get(clientId)).setStart(false, null, nscan);
            return true;
        }catch(ClassCastException e){
            System.err.println("you casted a speaker to a client, what's going on?");
            return false;
        }
    }

    //--------------------------------CONNECTEDWEBDEVICES----------------------------------------------------
    public List<Pair<Session, Device>> getConnectedWebSpeakers(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Speaker)
                    .collect(Collectors.toList());
    }

    public List<String> getConnectedWebSpeakersName(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Speaker)
                    .map(pair -> ((Speaker)pair.getRight()).getName())
                    .collect(Collectors.toList());
    }

    public List<Pair<Session, Device>> getConnectedWebClients(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Client)
                    .collect(Collectors.toList());
    }

    public Speaker getConnectedSpeaker(String id){
        return (Speaker) getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Speaker)
                    .filter(pair -> ((Speaker)pair.getRight()).getId().equals(id))
                    .findAny().get().getRight();
    }

    public List<Speaker> getConnectedSpeakerRoom(String roomId){
        List<Speaker> ret = new ArrayList<>();
        getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Speaker)
                    .filter(pair -> ((Speaker)pair.getRight()).getRoom().equals(roomId))
                    .collect(Collectors.toList()).forEach(pair -> ret.add((Speaker)pair.getRight()));
        return ret;
    }

    public List<Pair<Session, Device>> getConnectedWebDevices(){
        return connectedWebDevices.entrySet().stream()
                                    .map( (entry) -> new ImmutablePair<Session, Device>(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList()); // Streams are immutable
    }


    public void addConnectedWebDevice(Session session, MsgHello initMessage){
        Device newDevice;
        if(initMessage.getDeviceType() == 0)
            newDevice = new Client(initMessage.getId());
        else
            newDevice = new Speaker(initMessage.getId(), initMessage.getName());

        // Create Device if not present
        devices.putIfAbsent(initMessage.getId(), newDevice);
        connectedWebDevices.putIfAbsent(session, newDevice);
    }

    public void removeConnectedWebDevices(String deviceId){
        for(Pair<Session, Device> pair : getConnectedWebDevices())
            if(pair.getRight().getId() == deviceId)
                connectedWebDevices.remove(pair.getLeft());
    }

    // return deviceId
    public Device removeConnectedWebDevice(Session session){
        Device old = connectedWebDevices.remove(session);
        if(old != null)
            return old;
        return null;
    }

    public Device getConnectedWebDevice(Session session){
        return connectedWebDevices.get(session);
    }

    //--------------------------------CONNECTEDSOCKETDEVICES---------------------------------------------------
    public List<Client> getConnectedSocketClients(){
        return connectedSocketDevices.values().stream().collect(Collectors.toList()); // Streams are immutable
    }

    public void addConnectedSocketClient(String mac, MsgHello initMessage){
        Client newDevice = new Client(initMessage.getId());
        // Create Device if not present
        devices.putIfAbsent(initMessage.getId(), newDevice);
        connectedSocketDevices.putIfAbsent(initMessage.getId(), (Client) devices.get(initMessage.getId()));
    }

    public void removeConnectedSocketClient(String clientId){
        connectedSocketDevices.remove(clientId);
    }

    public boolean isConnectedSocket(String clientId){

        return connectedSocketDevices.containsKey(clientId);
    }

    //------------------------------SESSIONS-------------------------------------------
    /*public void addSession(Session session, String id){
        sessions.putIfAbsent(session, id);
    }

    public String removeSessions(Session session){
        return sessions.remove(session);
    }

    public long countSessions(String id){
        return sessions.values().stream().filter(val -> val.equals(id)).count();
    }*/

    //-------------------------------ROOMS-----------------------------------------
    public void setClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> newRoom = new ConcurrentHashMap<>();
        System.out.println("New room for client "+ clientId + ", roomID: " + roomId);
        newRoom.put(roomId.toLowerCase(), new Room(roomId));
        clientScans.put(clientId, newRoom);
    }

    public void deleteClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms;
        try{
            rooms = clientScans.get(clientId);
        }catch(NullPointerException e){
            System.out.println("Delete unexisting client (can happen don't worry)");
            return;
        }
        if( rooms == null ) return;
            rooms.remove(roomId);
        
    }

    public List<Room> getClientRooms(String clientId){
        ConcurrentHashMap<String, Room> rooms = clientScans.get(clientId);
        if( rooms == null ) return null;
        return new ArrayList<>(rooms.values());
    }

    public void putScans(String clientId, String roomId, List<APInfo> scans, int nscan){
        roomId = roomId.toLowerCase();
    
        Map<String, List<Double>> signals = new HashMap<>();//list of all the signals strength for the same ap in the same scan
        Map<String, ScanResult> results = new HashMap<>(); //utility map to retrieve info later on
        for(APInfo ap : scans){
            //create a list of results for each scan
            List<Double> listSignals = signals.get(ap.getBSSID());
            results.putIfAbsent(ap.getBSSID(), new ScanResult(ap.getBSSID(), ap.getSSID(), 0d, ap.getFrequency(), System.currentTimeMillis()));
            if(listSignals == null){
                listSignals = new ArrayList<>();
                listSignals.add(ap.getSignal());
                signals.put(ap.getBSSID(), listSignals);
            }else{
                listSignals.add(ap.getSignal());
            }
        }
        //orering the keys for the reference point so that the values are ordered for accesspoint id
        //helpful later
        String[] orderedKeys = ((String[]) signals.keySet().toArray());
        Arrays.sort(orderedKeys);
        clientScans.get(clientId).get(roomId).setNScan(nscan);
        for(String key : orderedKeys){
            //compute the mean for each scan
            double mean = signals.get(key).stream().reduce(0d, Double::sum)/signals.get(key).size();
            ScanResult finalResult = new ScanResult(key, results.get(key).getSSID(), mean, results.get(key).getFrequency(), results.get(key).getTimestamp());
            clientScans.get(clientId).get(roomId).putClientFingerprints(finalResult, nscan);
        }    
    }

    public void removeScans(String clientId, String roomId){
        roomId = roomId.toLowerCase();
        System.out.println("removing room:" + roomId);
    }

    public void printFingerprintDb(String clientId){
        for(String clientKey : clientScans.keySet()){
            for(String roomKey : clientScans.get(clientKey).keySet()){
                System.out.println("room: " + roomKey);
                clientScans.get(clientKey).get(roomKey).printFingerprints();
            }
        }
    }

    //-------------------------------SONGS-----------------------------------------
    public List<Song> getSongs(){
        return this.songs;
    }

}
