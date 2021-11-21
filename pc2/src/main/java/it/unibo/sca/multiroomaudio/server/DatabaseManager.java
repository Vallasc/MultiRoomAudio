package it.unibo.sca.multiroomaudio.server;


import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.dto.Device;

public class DatabaseManager {
    /*id, device*/
    private ConcurrentHashMap<String, Device> connectedDevices = new ConcurrentHashMap<String, Device>();
    
    /*
    THIS SHOULD GO IN ANOTHER OBJECT IF I WANT TO USE SYNCHRONIZED
    id (MAC), same as above, related fingerprint for the client<roomId, fingerprintlist>*/
    //private ConcurrentHashMap<String, HashMap<String, List<Fingerprint>>> fingerprints = new ConcurrentHashMap<>();
    /*it's like an async implementation idk*/

    /*utils*/
    /**
     * @method printConnect: prints the connected devices at a given moment
     */
    public void printConnected(){
        for(String key: connectedDevices.keySet())
            System.out.println(key);
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
        return connectedDevices.remove(id)==null;
    }
    /**
     * @method removeConnected: removes the id that corresponds to the session from the connectedDevices hashmap
     * @param session the session that corresponds to the mac (id)
     * @return true if the device was removed, false otherwise
     */
    public synchronized boolean removeConnected(Session session){
        for(String key : connectedDevices.keySet()){
            if(connectedDevices.get(key).getSession().equals(session)){
                connectedDevices.remove(key);
                return true;
            }
        }
        return false;
    }

    public void setSession(String id, Session session){
        connectedDevices.get(id).setSession(session);
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
    


    /*
    public void putFingerprints(String id, ArrayList<Fingerprint> fingerprintlist){
        
        fingerprints.put(id, new HashMap<String, List<Fingerprint>>());

    }*/

}
