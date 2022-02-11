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
import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgBindSpeaker;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgScanRoomDone;
import it.unibo.sca.multiroomaudio.shared.model.*;

public class DatabaseManager {
    private final List<Song> songs = new ArrayList<Song>();
    private final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server
    private final ConcurrentHashMap<Session, Device> connectedWebDevices = new ConcurrentHashMap<>(); //should be to save the sessions 
    private final ConcurrentHashMap<String, Client> connectedSocketDevices = new ConcurrentHashMap<>(); //all the connected socket devices
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Room>> clientRooms = new ConcurrentHashMap<>(); //the rooms for each client

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

    public List<Speaker> getConnectedSpeakerRoom(String clientId, String roomId){
        try {
            return clientRooms.get(clientId).get(roomId)
                                .getSpeakerList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
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

    public void removeConnectedWebDevicesAndDisconnect(String deviceId){
        for(Pair<Session, Device> pair : getConnectedWebDevices()){
            if(pair.getRight().getId().equals(deviceId)){
                connectedWebDevices.remove(pair.getLeft());
                pair.getLeft().close();
            }
        }
    }

    public Device removeConnectedWebDevice(Session session){
        return connectedWebDevices.remove(session);
    }

    public Device getConnectedWebDevice(Session session){
        return connectedWebDevices.get(session);
    }

    public Session getClientWebSession(String clientId){
        for( Pair<Session, Device> pair : getConnectedWebClients()) {
            if( pair.getRight().getId().equals(clientId)){
                return pair.getLeft();
            }
        }
        return null;
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

    //-------------------------------ROOMS-----------------------------------------
    public void setClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms;
        rooms = clientRooms.get(clientId);
        if(rooms == null){
            rooms = new ConcurrentHashMap<String, Room>();
            rooms.put(roomId.toLowerCase(), new Room(roomId));
            clientRooms.putIfAbsent(clientId, rooms);
        }
        else
            rooms.put(roomId.toLowerCase(), new Room(roomId));

        System.out.println("New room for client "+ clientId + ", roomID: " + roomId);
    }

    public void deleteClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms;
        try{
            rooms = clientRooms.get(clientId);
        }catch(NullPointerException e){
            System.out.println("Delete unexisting client (can happen don't worry)");
            return;
        }
        if( rooms == null ) return;
            rooms.remove(roomId);
        
    }

    public List<Room> getClientRooms(String clientId){
        ConcurrentHashMap<String, Room> rooms = clientRooms.get(clientId);
        if( rooms == null ) return new ArrayList<>();
        return new ArrayList<>(rooms.values());
    }

    public void putScans(Client client, ScanResult[] scans){
        if(client.getCurrentPositionScans() == 0){
            client.getCurrentTmpScans().clear();
        }

        Session clientSession = getClientWebSession(client.getId());
        if(clientSession == null || client.getActiveRoom() == null){ // stop scanning process
            return;
        }

        String roomId = client.getActiveRoom().toLowerCase();
        Room room = clientRooms.get(client.getId()).get(roomId);
        int currentPositionScans = client.getCurrentPositionScans();
        boolean doneScan = false;
        // If room is not full
        if(room.getNScan() < Room.MAX_POSITION){
            // If corner is not full
            if(currentPositionScans < Room.SCANS_FOR_EACH_POSITION){
                client.getCurrentTmpScans().addAll(Arrays.asList(scans));
                currentPositionScans++;
                client.setCurrentPositionScans(currentPositionScans);
            } else { // corner is full
                // Save corner scans
                room.setNScan(room.getNScan() + 1);
                putScansUpdateRoom(client.getId(), roomId, client.getCurrentTmpScans());
                try {
                    // Done scan corner
                    WebSocketHandler.sendMessage(clientSession, 
                        new MsgScanRoomDone(false, true));
                } catch (Exception e) {}
                // Reset corner scan counter
                client.setCurrentPositionScans(0);
                client.setActiveRoom(null);
                if(room.getNScan() >= Room.MAX_POSITION)
                    doneScan = true;
            }
        } else {
            doneScan = true;
            client.setActiveRoom(null);
            client.setCurrentPositionScans(0);
        }
        // Done room scan
        if(doneScan){
            try {
                WebSocketHandler.sendMessage(clientSession, 
                    new MsgScanRoomDone(true, true));
            } catch (Exception e) {}
        }

        //System.out.println("CURRENT SCAN CORNER= " + room.getNScan());
        //System.out.println("CURRENT CORNER SCAN INDEX= " + currentPositionScans);
    }

    // Set scans for a room
    public void putScansUpdateRoom(String clientId, String roomId, List<ScanResult> scans){ 
        /*Room room = clientScans.get(clientId).get(roomId);
        room.setNScan(room.getNScan() + 1);*/
        Map<String, List<Double>> signals = new HashMap<>();//list of all the signals strength for the same ap in the same scan
        Map<String, ScanResult> results = new HashMap<>(); //utility map to retrieve info later on
        for(ScanResult scan : scans){
            //create a list of results for each scan
            List<Double> listSignals = signals.get(scan.getBSSID());
            results.putIfAbsent(scan.getBSSID(), scan);
            if(listSignals == null){
                listSignals = new ArrayList<>();
                listSignals.add(scan.getSignal());
                signals.put(scan.getBSSID(), listSignals);
            }else{
                listSignals.add(scan.getSignal());
            }
        }
        
        //if i want to put the variance in it 
        for(String key : signals.keySet()){
            //compute the mean for each scan+
            ScanResult finalResult;
            double mean = signals.get(key).stream().reduce(0d, Double::sum)/signals.get(key).size();
            finalResult = new ScanResult(key, results.get(key).getSSID(), mean, results.get(key).getFrequency(), results.get(key).getTimestamp());
            clientRooms.get(clientId).get(roomId).putClientFingerprints(finalResult);
        }
    }

    public void printFingerprintDb(String clientId){
        for(String clientKey : clientRooms.keySet()){
            for(String roomKey : clientRooms.get(clientKey).keySet()){
                System.out.println("room: " + roomKey);
                clientRooms.get(clientKey).get(roomKey).printFingerprints();
            }
        }
    }

    //-------------------------------SONGS-----------------------------------------
    public List<Song> getSongs() {
        return this.songs;
    }

    public void bindSpeaker(String clientId, String speakerId, String roomId) {
        Speaker speaker = this.getConnectedSpeaker(speakerId);
        ConcurrentHashMap<String, Room> rooms = clientRooms.get(clientId);
        if(rooms == null || speaker == null) return;

        for(Room room : rooms.values()) {
            List<Speaker> speakers = room.getSpeakerList();
            speakers.remove(speaker);
            if(room.getId().equals(roomId)){
                speakers.add(speaker);
            }
        }

        System.out.println("DEBUG: bind speaker\n\t" + speaker.getName() + "<->"+ roomId + ", is muted: " + speaker.isMuted());
    }

}
