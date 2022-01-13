package it.unibo.sca.multiroomaudio.server;


import java.util.ArrayList;
import java.util.List;
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

    public boolean setDeviceStart(String clientId, String roomId){
        setDeviceStop(clientId);
        try{
            setClientRoom(clientId, roomId);
            ((Client) devices.get(clientId)).setStart(true, roomId);
            return true;
        }catch(ClassCastException e){
            System.err.println("you casted a speaker to a client, what's going on?");
            return false;
        }
    }

    public boolean setDeviceStop(String clientId){
        try{
            ((Client) devices.get(clientId)).setStart(false, null);
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

    public List<Pair<Session, Device>> getConnectedWebClients(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Client)
                    .collect(Collectors.toList());
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
        newRoom.putIfAbsent(roomId.toLowerCase(), new Room(roomId));
        ConcurrentHashMap<String, Room> presentHM = clientScans.putIfAbsent(clientId, newRoom);
        //there was already the hashmap
        if(presentHM != null){
            presentHM.putIfAbsent(roomId, new Room(roomId));
        }
    }

    public void deleteClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms = clientScans.get(clientId);
        if( rooms == null ) return;
        rooms.remove(roomId);
    }

    public List<Room> getClientRooms(String clientId){
        ConcurrentHashMap<String, Room> rooms = clientScans.get(clientId);
        if( rooms == null ) return null;
        return new ArrayList<>(rooms.values());
    }

    public void putScans(String clientId, String roomId, APInfo[] scans){
        roomId = roomId.toLowerCase();
        clientScans.get(clientId).get(roomId).putClientFingerprints(scans);
    }

    public void removeScans(String clientId, String roomId){
        roomId = roomId.toLowerCase();
        clientScans.get(clientId).remove(roomId);
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
