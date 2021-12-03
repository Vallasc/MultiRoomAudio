package it.unibo.sca.multiroomaudio.server;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.shared.dto.*;
/*HashMap<id, Device> (tutti i device)
list<device> (device connessi)
Map<clientID, stanza> -> Stanza(Nome, Map<idClient, List<Fingerprint>>)  -> 
Device -> se il device è un client il fingerprint va salvato dentro device e il thread che si occupa del calcolo gira sui device connessi
        che sono client
        alcune robe che vengono accedute da device devono essere accedute con metodi synchronized
MusicOrchestrationManager -> (list<speaker>, minutaggio, canzone)*/
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;

public class DatabaseManager {

    private final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server
    private final ConcurrentHashMap<String, Pair<Session, Device>> connectedWebDevices = new ConcurrentHashMap<>(); //all the connected web devices
    private final ConcurrentHashMap<String, Client> connectedSocketDevices = new ConcurrentHashMap<>(); //all the connected socket devices
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Room>> clientScans = new ConcurrentHashMap<>(); //the rooms for each client
    private final ConcurrentHashMap<Session, String> sessions = new ConcurrentHashMap<>(); //should be to save the sessions 

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
        try{
            setClientRoom(clientId,roomId);
            ((Client) devices.get(clientId)).setStart(true, roomId);
            return true;
        }catch(ClassCastException e){
            System.err.println("you casted a speaker to a client, what's going on?");
            return false;
        }
    }

    public boolean getDeviceStart(String key){
        try{
            return ((Client) devices.get(key)).getStart();
        }catch(ClassCastException e){
            System.err.println("you casted a speaker to a client, what's going on?");
            return false;
        }
    }

    public boolean setDeviceStop(String key){
        try{
            ((Client) devices.get(key)).setStart(false, null);
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
        return connectedWebDevices.values().stream().collect(Collectors.toList()); // Streams are immutable
    }

    /*public void addConnectedWebDevice(Session session, String deviceId){
        connectedWebDevices.putIfAbsent(deviceId, new ImmutablePair<Session, Device>(session, devices.get(deviceId)));
    }*/

    public void addConnectedWebDevice(Session session, MsgHello initMessage){
        Device newDevice;
        if(initMessage.getDeviceType() == 0)
            newDevice = new Client(initMessage.getId());
        else
            newDevice = new Speaker(initMessage.getId(), initMessage.getName());

        // Create Device if not present
        devices.putIfAbsent(initMessage.getId(), newDevice);
        connectedWebDevices.putIfAbsent(initMessage.getId(), 
            new ImmutablePair<Session, Device>(session, devices.get(initMessage.getId())));
    }

    public void removeConnectedWebDevice(String deviceId){
        connectedWebDevices.remove(deviceId);
    }

    public String removeConnectedWebDevice(Session session){
        List<String> found = connectedWebDevices.values().stream()
                            .filter(pair -> pair.getLeft() == session)
                            .map(pair -> pair.getRight().getId())
                            .collect(Collectors.toList());
        System.out.println(found.size());
        if(found.size() == 1){
            connectedWebDevices.remove(found.get(0));
            return found.get(0);
        }else if(found.size() > 1)
            connectedWebDevices.remove(found.get(0));
        return null;
    }

    public boolean isConnectedWeb(String deviceId){
        return connectedWebDevices.containsKey(deviceId);
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
    public void addSession(Session session, String id){
        sessions.putIfAbsent(session, id);
    }

    public String removeSessions(Session session){
        return sessions.remove(session);
    }

    public long countSessions(String id){
        return sessions.values().stream().filter(val -> val.equals(id)).count();
    }

    //-------------------------------ROOMS-----------------------------------------
    public void setClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> newRoom = new ConcurrentHashMap<>();
        System.out.println("room: " + roomId);
        newRoom.putIfAbsent(roomId, new Room(roomId));
        ConcurrentHashMap<String, Room> presentHM = clientScans.putIfAbsent(clientId, newRoom);
        //there was already the hashmap
        if(presentHM != null){
            presentHM.putIfAbsent(roomId, new Room(roomId));
        }
    }

    public void putScans(String clientId, String roomId, APInfo[] scans){
        clientScans.get(clientId).get(roomId).putClientFingerprints(scans);
    }

    public void printFingerprintDb(String clientId){
        for(String clientKey : clientScans.keySet()){
            for(String roomKey : clientScans.get(clientKey).keySet()){
                System.out.println("room: " + roomKey);
                clientScans.get(clientKey).get(roomKey).printFingerprints();
            }
        }
    }

}
