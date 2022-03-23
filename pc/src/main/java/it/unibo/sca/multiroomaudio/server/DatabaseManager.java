package it.unibo.sca.multiroomaudio.server;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;
import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgScanRoomDone;
import it.unibo.sca.multiroomaudio.shared.model.*;
import it.unibo.sca.multiroomaudio.utils.Utils;

/**
 * Handles storage of devices, rooms and speakers.
 * Implements methods to retrieve them in a easy way.
 */
public class DatabaseManager {
    private final List<Song> songs = new ArrayList<Song>();
    private final ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<>(); //all the devices seen by the server
    private final ConcurrentHashMap<Session, Device> connectedWebDevices = new ConcurrentHashMap<>(); //should be to save the sessions 
    private final ConcurrentHashMap<String, Client> connectedSocketDevices = new ConcurrentHashMap<>(); //all the connected socket devices
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Room>> clientRooms = new ConcurrentHashMap<>(); //the rooms for each client

    // Devices

    /**
     * Get device by key
     * @param key device id
     * @return device object
     */
    public Device getDevice(String key){
        return devices.get(key);
    }

    /**
     * Get all registered devices
     * @return devices map
     */
    public HashMap<String, Device> getDevices(){
        HashMap<String, Device> toSave = new HashMap<>();
        int i = 0;
        for(String key : devices.keySet()){
            toSave.put(i+"", devices.get(key));
            i++;
        }
        return toSave;
    }

    /**
     * Set a specific device
     * @param device Device object
     */
    public void setDevices(Device device){
        this.devices.put(device.getId(), device);
    }

    // Web devices

    /**
     * Get all connected web speakers
     * @return list of <session, speaker device>
     */
    public List<Pair<Session, Device>> getConnectedWebSpeakers(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Speaker)
                    .collect(Collectors.toList());
    }

    /**
     * Get all connected web clients
     * @return list of <session, client device>
     */
    public List<Pair<Session, Device>> getConnectedWebClients(){
        return getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Client)
                    .collect(Collectors.toList());
    }

    /**
     * Get a connected speaker by its id
     * @param id speaker id
     * @return Speaker object
     */
    public Speaker getConnectedSpeaker(String id){
        return (Speaker) getConnectedWebDevices().stream()
                    .filter(pair -> pair.getRight() instanceof Speaker)
                    .filter(pair -> ((Speaker)pair.getRight()).getId().equals(id))
                    .findAny().get().getRight();
    }

    /**
     * Get all connected speaker of clientId present in roomId
     * @param clientId client id
     * @param roomId room id
     * @return list of speakers
     */
    public List<Speaker> getConnectedSpeakersRoom(String clientId, String roomId){
        try {
            return clientRooms.get(clientId).get(roomId.toLowerCase())
                                .getSpeakerList();
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Get all connected web devices
     * @return list of <session, device>
     */
    public List<Pair<Session, Device>> getConnectedWebDevices(){
        return connectedWebDevices.entrySet().stream()
                                    .map( (entry) -> new ImmutablePair<Session, Device>(entry.getKey(), entry.getValue()))
                                    .collect(Collectors.toList()); // Streams are immutable
    }

    /**
     * Set a web device connected
     * @param session device session
     * @param initMessage HelloMessage
     */
    public void addConnectedWebDevice(Session session, MsgHello initMessage){
        Device newDevice;
        if(initMessage.getDeviceType() == 0)
            newDevice = new Client(initMessage.getId());
        else
            newDevice = new Speaker(initMessage.getId(), initMessage.getName());

        // Create Device if not present
        devices.putIfAbsent(initMessage.getId(), newDevice);
        Device oldDevice = devices.get(initMessage.getId());
        connectedWebDevices.put(session, oldDevice);
    }

    /**
     * Remove web device from connected map
     * @param deviceId device id
     */
    public void removeConnectedWebDevicesAndDisconnect(String deviceId){
        for(Pair<Session, Device> pair : getConnectedWebDevices()){
            if(pair.getRight().getId().equals(deviceId)){
                connectedWebDevices.remove(pair.getLeft());
                pair.getLeft().close();
            }
        }
    }

    /**
     * Remove web device from connected map using session
     * @param session device session
     * @return removed Device
     */
    public Device removeConnectedWebDevice(Session session){
        return connectedWebDevices.remove(session);
    }

    /**
     * Get a connected web device by his session
     * @param session device session
     * @return Device object
     */
    public Device getConnectedWebDevice(Session session){
        return connectedWebDevices.get(session);
    }

    /**
     * Get session of a client
     * @param clientId  client id
     * @return Session object
     */
    public Session getClientWebSession(String clientId){
        for( Pair<Session, Device> pair : getConnectedWebClients()) {
            if( pair.getRight().getId().equals(clientId)){
                return pair.getLeft();
            }
        }
        return null;
    }

    // Socket devices

    /**
     * Get all socket clients
     * @return list of socket clients
     */
    public List<Client> getConnectedSocketClients(){
        return connectedSocketDevices.values().stream().collect(Collectors.toList()); // Streams are immutable
    }

    /**
     * Add a connected socket client
     * @param initMessage HelloMessage
     */
    public void addConnectedSocketClient(MsgHello initMessage){
        Client newDevice = new Client(initMessage.getId());
        // Create Device if not present
        devices.putIfAbsent(initMessage.getId(), newDevice);
        connectedSocketDevices.putIfAbsent(initMessage.getId(), (Client) devices.get(initMessage.getId()));
    }

    /**
     * Remove a connected socket client
     * @param clientId client id
     */
    public void removeConnectedSocketClient(String clientId){
        connectedSocketDevices.remove(clientId);
    }

    /**
     * Check if a certain id is connected by socket
     * @param clientId client id
     * @return true if clientId is connected by socket, false otherwise
     */
    public boolean isConnectedSocket(String clientId){
        return connectedSocketDevices.containsKey(clientId);
    }

    // Rooms

    /**
     * Create room and bind to clientId
     * @param clientId client id
     * @param roomId room id
     */
    public void setClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms;
        rooms = clientRooms.get(clientId);
        if(rooms == null){
            rooms = new ConcurrentHashMap<String, Room>();
            rooms.put(roomId.toLowerCase(), new Room(roomId.toLowerCase()));
            clientRooms.putIfAbsent(clientId, rooms);
        }
        else
            rooms.put(roomId.toLowerCase(), new Room(roomId.toLowerCase()));

        System.out.println("New room for client "+ clientId + ", roomID: " + roomId);
    }

    /**
     * Set webhooks for a certain room
     * @param clientId client id
     * @param roomId room id
     * @param urlEnter url on room enter
     * @param urlLeave url on room leave
     */
    public void setRoomUrls(String clientId, String roomId, String urlEnter, String urlLeave){
        ConcurrentHashMap<String, Room> rooms;
        rooms = clientRooms.get(clientId);
        if(rooms != null){
            Room room = rooms.get(roomId);
            if(rooms != null){
                room.setUrlEnter(urlEnter);
                room.setUrlLeave(urlLeave);
            }
        }
    }

    /**
     * Delete a room
     * @param clientId clieint id
     * @param roomId room id
     */
    public void deleteClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms;
        try{
            rooms = clientRooms.get(clientId);
        }catch(NullPointerException e){
            System.out.println("Delete unexisting client (can happen don't worry)");
            return;
        }
        if( rooms == null ) return;
        rooms.remove(roomId.toLowerCase());        
    }

    /**
     * Get all rooms for clientId
     * @param clientId client id
     * @return list of rooms
     */
    public List<Room> getClientRooms(String clientId){
        ConcurrentHashMap<String, Room> rooms = clientRooms.get(clientId);
        if( rooms == null ) return new ArrayList<>();
        return new ArrayList<>(rooms.values());
    }

    /**
     * Get a certain room of clientId
     * @param clientId client id
     * @param roomId room id
     * @return  Room object
     */
    public Room getClientRoom(String clientId, String roomId){
        ConcurrentHashMap<String, Room> rooms = clientRooms.get(clientId);
        if( rooms == null ) return null;
        return rooms.get(roomId);
    }

    /**
     * Save scans for current active client room
     * @param client Client object
     * @param scans Array of scans
     */
    public void saveRoomScans(Client client, ScanResult[] scans){
        MsgScanRoomDone doneMessage = null;
        if(client.getCurrentPositionScans() == 0)
            client.getCurrentTmpScans().clear();

        Session clientSession = getClientWebSession(client.getId());
        if(clientSession == null || client.getActiveRoom() == null) // Stop scanning process
            return;

        String roomId = client.getActiveRoom().toLowerCase();
        Room room = clientRooms.get(client.getId()).get(roomId);
        int currentPositionScans = client.getCurrentPositionScans();
        // If room is not full
        if(room.getNScan() < Room.MAX_POSITION){
            // If corner is not full
            if(currentPositionScans < Room.SCANS_FOR_EACH_POSITION){
                client.getCurrentTmpScans().addAll(Arrays.asList(scans));
                currentPositionScans++;
                client.setCurrentPositionScans(currentPositionScans);
            } else { // Corner is full
                // Save corner scans
                updateRoomFingerprints(client.getId(), roomId, client.getCurrentTmpScans());
                doneMessage = new MsgScanRoomDone(false, true);
                if(room.getNScan() >= Room.MAX_POSITION)
                    doneMessage = new MsgScanRoomDone(true, true);
            }
        } else {
            doneMessage = new MsgScanRoomDone(true, true);
        }
        if(doneMessage != null){
            // Reset corner scan counter
            client.setCurrentPositionScans(0);
            client.setActiveRoom(null);
            if(doneMessage.isAllRoomDone())
                client.setOffline(false);
            try {
                WebSocketHandler.sendMessage(clientSession, doneMessage);
            } catch (Exception e) {}
        }
    }

    /**
     * Update directly room fingerprints
     * @param clientId client id
     * @param roomId room id
     * @param scans AP scans
     */
    public void updateRoomFingerprints(String clientId, String roomId, List<ScanResult> scans){
        // Calculate mean stdev and aggregate samples
        scans = Utils.computeMeanFingeprint(scans);
        Room room = clientRooms.get(clientId).get(roomId);
        room.putFingerprints(scans);
    }

    /**
     * Update directly room fingerprints and clean confirmation array
     * @param client client Object
     * @param roomId room id
     */
    public void updateRoomFingerprintsConfirm(Client client, String roomId){
        Room room = clientRooms.get(client.getId()).get(roomId);
        room.putFingerprints(client.getConfirmationFingerprints());
        client.clearConfirmation();
    }

    /**
     * Print fingerprints of clientId
     * @param clientId client id
     */
    public void printFingerprintDb(String clientId){
        for(String clientKey : clientRooms.keySet()){
            for(String roomKey : clientRooms.get(clientKey).keySet()){
                System.out.println("room: " + roomKey);
                clientRooms.get(clientKey).get(roomKey).printFingerprints();
            }
        }
    }

    /**
     * Get all client -> Rooms map
     * @return client-rooms map
     */
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Room>> getClientRooms(){
        return this.clientRooms;
    }

    /**
     * Resume fingerprint from json
     * @param json JsonObject
     */
    public void putFingerprintsResume(JsonObject json){
        Gson gson = new Gson();
        Type scanType = new TypeToken<ArrayList<ScanResult>>(){}.getType();
        //Type speakerType = new TypeToken<ArrayList<Speaker>>(){}.getType();
        for(String clientId : json.keySet()){
            JsonObject clientObj = json.get(clientId).getAsJsonObject();
            this.clientRooms.put(clientId, new ConcurrentHashMap<>());
            for(String roomId : clientObj.keySet()){
                JsonObject roomObj = clientObj.get(roomId).getAsJsonObject();
                
                JsonObject fingerprintsObj = roomObj.get("fingerprints").getAsJsonObject();
                
                HashMap<String, List<ScanResult>> fingerprints =  new HashMap<>();
                for(String bssid : fingerprintsObj.keySet()){
                    JsonArray fingerprintsArr = fingerprintsObj.get(bssid).getAsJsonArray();
                    List<ScanResult> result = gson.fromJson(fingerprintsArr, scanType);
                    fingerprints.put(bssid, result);
                }
                int nscan = roomObj.get("nscan").getAsInt();
                Room room = new Room(roomId, fingerprints, nscan);
                this.clientRooms.get(clientId).put(roomId, room);
            }
        }
    }

    // Songs

    /**
     * Get all songs
     * @return list of songs
     */
    public List<Song> getSongs() {
        return this.songs;
    }

    /**
     * Bind speaker to a certain room
     * @param clientId client id
     * @param speakerId speaker id
     * @param roomId room id
     */
    public void bindSpeaker(String clientId, String speakerId, String roomId) {
        roomId = roomId.toLowerCase();
        Speaker speaker = this.getConnectedSpeaker(speakerId);
        ConcurrentHashMap<String, Room> rooms = clientRooms.get(clientId);
        if(rooms == null || speaker == null){
            System.out.println("DEBUG: no speaker bound");
            return;
        }
        System.out.println("DEBUG: bind speaker\n\t" + speaker.getName() + " <-> "+ roomId + ", is muted: " + speaker.isMuted());
        System.out.println("DEBUG: room -> [speakers]");
        for(Room room : rooms.values()) {
            List<Speaker> speakers = room.getSpeakerList();
            speakers.remove(speaker);
            if(room.getId().equals(roomId)){
                speakers.add(speaker);
            }
            System.out.print("\t" + room.getId() + " -> [");
            for(Speaker spk : speakers)
                System.out.print(spk.getName() + ", ");
            System.out.println("]");
        }
    }

    /**
     * Unbind speaker
     * @param clientId client id
     * @param speakerId speaker id
     */
    public void unbindSpeaker(String clientId, String speakerId) {
        Speaker speaker = this.getConnectedSpeaker(speakerId);
        ConcurrentHashMap<String, Room> rooms = clientRooms.get(clientId);
        if(rooms == null || speaker == null){
            System.out.println("DEBUG: no speaker binded");
            return;
        }
        System.out.println("DEBUG: unbind speaker [" + speaker.getName() + "]");
        for(Room room : rooms.values()) {
            room.getSpeakerList().remove(speaker);
        }
    }

}
