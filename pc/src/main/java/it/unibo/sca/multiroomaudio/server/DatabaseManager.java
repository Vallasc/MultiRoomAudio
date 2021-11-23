package it.unibo.sca.multiroomaudio.server;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.dto.*;
/*HashMap<id, Device> (tutti i device)
list<device> (device connessi)
Map<stanzaID, stanza> -> Stanza(Nome, Map<idClient, List<Fingerprint>>)
Device -> se il device è un client il fingerprint va salvato dentro device e il thread che si occupa del calcolo gira sui device connessi
        che sono client
        alcune robe che vengono accedute da device devono essere accedute con metodi synchronized
MusicOrchestrationManager -> (list<speaker>, minutaggio, canzone)*/
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;

public class DatabaseManager {

    private final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server
    private final ConcurrentHashMap<String, Pair<Session, Device>> connectedWebDevices = new ConcurrentHashMap<>(); //all the connected web devices
    private final ConcurrentHashMap<String, Client> connectedSocketDevices = new ConcurrentHashMap<>(); //all the connected socket devices
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>(); //dunno if concurrent

    public String getKeyDevice(String id){
        // Can throw ConcurrentModificationException if an element is deleted during the iteration
        // but we don't delete devices :)
        for(String key : devices.keySet())
            if(devices.get(key).getId().equals(id))
                return key;
        return null;
    }

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

    public void removeConnectedWebDevice(Session session){
        List<String> found = connectedWebDevices.values().stream()
                            .filter(pair -> pair.getLeft() == session)
                            .map(pair -> pair.getRight().getId())
                            .collect(Collectors.toList());
        if(found.size() > 0)
            connectedWebDevices.remove(found.get(0));
    }

    public boolean isConnectedWeb(String deviceId){
        return connectedWebDevices.containsKey(deviceId);
    }

    //--------------------------------------------------------------------------------------------------------------------------------
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
}
