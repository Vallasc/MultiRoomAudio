package it.unibo.sca.multiroomaudio.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import it.unibo.sca.multiroomaudio.shared.dto.*;
/*HashMap<id, Device> (tutti i device)
list<device> (device connessi)
Map<stanzaID, stanza> -> Stanza(Nome, Map<idClient, List<Fingerprint>>)
Device -> se il device è un client il fingerprint va salvato dentro device e il thread che si occupa del calcolo gira sui device connessi
        che sono client
        alcune robe che vengono accedute da device devono essere accedute con metodi synchronized
MusicOrchestrationManager -> (list<speaker>, minutaggio, canzone)*/

public class DatabaseManager {

    public static ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server
    public static LinkedBlockingQueue<Device> connectedDevices = new LinkedBlockingQueue<>(); 
    public static ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>(); //dunno if concurrent
}
