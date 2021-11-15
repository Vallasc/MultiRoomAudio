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
    /*<mac, id>, these two are used only if the server uses different ids from the mac, for translation, otherwise is useless*/
    public static ConcurrentHashMap<String, String> ids = new ConcurrentHashMap<>();
    public static volatile String id = "0000";

    /*utils*/
    /**
     * @method printConnect: prints the connected devices at a given moment
     */
    public void printConnected(){
        for(String key: connectedDevices.keySet())
            System.out.println(key);
    }
    /*ids*/
    
    public synchronized String getId(){
        return id;
    }

    public void putIds(String mac){
        ids.put(mac, id);
    }

    /*connected devices*/
    /**
     * @method putConnected: puts the given id into the connectedDevices hashmap, building a new empty device
     * @param id the mac (id) of the device
     * used just for debugging
     */
    public void putConnected(String id){
        connectedDevices.put(id, new Device()); 
    }

    /**
     * @method putConnected: puts the given id and device into the connectedDevices hashmap
     * @param id the mac (id) of the device
     * @param d the device that just connected
     */
    public void putConnected(String id, Device d){
        connectedDevices.put(id, d);
    }
    /**
     * @method removeConnected: removes the given id from the connectedDevices hashmap
     * @param id the mac (id) of the device
     * @return true if the device was removed, false otherwise
     */
    public boolean removeConnected(String id){
        if(connectedDevices.remove(id)==null)
            return false;
        return true;
    }
    /**
     * @method removeConnected: removes the id that corresponds to the session from the connectedDevices hashmap
     * @param session the session that corresponds to the mac (id)
     * @return true if the device was removed, false otherwise
     */
    public boolean removeConnected(Session session){
        for(String key : connectedDevices.keySet()){
            if(connectedDevices.get(key).getSession().equals(session)){
                connectedDevices.remove(key);
                return true;
            }
        }
        return false;
    }

    /**
     * @method isClientConnected
     * @return true if a client (type is 0) is connected, false otherwise
     */
    public boolean isClientConnected(){
        for(String key: connectedDevices.keySet()){
            if(connectedDevices.get(key).getType() == 0)
                return true;
        }
        return false;
    }

    /**
     * @method alreadyConnected: tells if the device with the given mac is already connected to the server
     * @param mac the mac address of the device
     * @return true if the device is already connected, false otherwise
     */
    public boolean alreadyConnected(String mac){
        return connectedDevices.containsKey(mac);
    }

    /*offline fingerprints*/
    /**
     * @method putFingerprints puts the fingerprint list for each device for each room inside firngerprintlist
     * @param id the id of the device
     * @param fingerprintlist fingerprintlist for a given room
     */
    public void putFingerprints(String id, ArrayList<Fingerprint> fingerprintlist){
        
        fingerprints.put(id, new HashMap<String, List<Fingerprint>>());

    }

}
