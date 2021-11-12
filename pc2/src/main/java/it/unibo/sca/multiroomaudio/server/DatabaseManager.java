package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;

public class DatabaseManager {
    /*id, device*/
    public static ConcurrentHashMap<String, Device> connectedDevices = new ConcurrentHashMap<String, Device>();
    /*id (MAC), same as above, related fingerprint for the client<roomId, fingerprintlist>*/
    public static ConcurrentHashMap<String, HashMap<String, List<Fingerprint>>> fingerprints = new ConcurrentHashMap<>();
    /*<mac, id>*/
    public static ConcurrentHashMap<String, String> ids = new ConcurrentHashMap<>();

    public static volatile String id = "0000";
    /*utils*/
    public void printConnected(){
        for(String key: connectedDevices.keySet())
            System.out.println(key);
    }

    public synchronized String getId(){
        return id;
    }
    /*ids*/
    public void putIds(String mac){
        ids.put(mac, id);
    }
    /*connected devices*/
    public void putConnected(String id){
        connectedDevices.put(id, new Device()); 
    }

    public void putConnected(String id, Device d){
        connectedDevices.put(id, d);
    }

    public boolean removeConnected(String id){
        if(connectedDevices.remove(id)==null)
            return false;
        return true;
    }

    public boolean removeConnected(Session session){
        for(String key : connectedDevices.keySet()){
            if(connectedDevices.get(key).getSession().equals(session)){
                connectedDevices.remove(key);
                return true;
            }
        }
        return false;
    }

    public boolean isClientConnected(){
        for(String key: connectedDevices.keySet()){
            if(connectedDevices.get(key).getType() == 0)
                return true;
        }
        return false;
    }

    public boolean alreadyConnected(String mac){
        return connectedDevices.containsKey(mac);
    }

    /*offline fingerprints*/
    
    public void putFingerprints(String id, ArrayList<Fingerprint> fingerprintlist){
        
        fingerprints.put(id, new HashMap<String, List<Fingerprint>>());

    }

}
