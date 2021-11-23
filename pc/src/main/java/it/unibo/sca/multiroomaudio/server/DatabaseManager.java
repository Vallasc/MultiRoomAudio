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

public class DatabaseManager {

    public final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server
    public final ConcurrentHashMap<String, Pair<Session, Device>> connectedWebDevices = new ConcurrentHashMap<>(); //all the connected web devices
    public final ConcurrentHashMap<String, Device> connectedSocketDevices = new ConcurrentHashMap<>(); //all the connected socket devices
    public final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>(); //dunno if concurrent

    public String getKeyDevice(String ip){
        // Can throw ConcurrentModificationException if an element is deleted during the iteration
        // but we don't delete devices :)
        for(String key : devices.keySet())
            if(devices.get(key).getIp().equals(ip))
                return key;
        return null;
    }

    public List<Pair<Session, Device>> getConnectedWebSpeakers(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight().getType() == 1)
                    .collect(Collectors.toList());
    }

    public List<Pair<Session, Device>> getConnectedWebClients(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight().getType() == 0)
                    .collect(Collectors.toList());
    }


    public List<Pair<Session, Device>> getConnectedWebDevices(){
        return connectedWebDevices.values().stream().collect(Collectors.toList()); // Streams are immutable
    }

    public void addConnectedWebDevice(Session session, String deviceId){
        connectedWebDevices.putIfAbsent(deviceId, new ImmutablePair<Session, Device>(session, devices.get(deviceId)));
    }

    public void removeConnectedWebDevice(String deviceId){
        connectedWebDevices.remove(deviceId);
    }

    public boolean isConnectedWeb(String deviceId){
        return connectedWebDevices.containsKey(deviceId);
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    public List<Device> getConnectedSocketDevices(){
        return connectedSocketDevices.values().stream().collect(Collectors.toList()); // Streams are immutable
    }

    public void addConnectedSocketDevice(String clientId){
        connectedSocketDevices.putIfAbsent(clientId, devices.get(clientId));
    }

    public void removeConnectedSocketDevice(String clientId){
        connectedSocketDevices.remove(clientId);
    }

    public boolean isConnectedSocket(String clientId){
        return connectedSocketDevices.containsKey(clientId);
    }
}
