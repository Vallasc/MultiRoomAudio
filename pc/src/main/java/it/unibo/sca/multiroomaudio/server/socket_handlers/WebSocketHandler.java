package it.unibo.sca.multiroomaudio.server.socket_handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.MusicOrchestrationManager;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPlay;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgBindSpeaker;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgConfirmationRoom;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgCreateRoom;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgDeleteRoom;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgRoomURL;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgRooms;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgScanRoom;
import it.unibo.sca.multiroomaudio.shared.messages.settings.MsgSettings;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Device;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final DatabaseManager dbm;  
    private final MusicOrchestrationManager musicManager;
    private final SpeakerManager speakerManager;

    public WebSocketHandler(DatabaseManager dbm, MusicOrchestrationManager musicManager, SpeakerManager speakerManager) {
        this.dbm = dbm;
        this.musicManager = musicManager;
        this.speakerManager = speakerManager;
    }

    public void handleClose(Session session){
        Device device = dbm.removeConnectedWebDevice(session);
        if(device != null){
            if(device instanceof Speaker){
                speakerManager.updateSpeakerList();
            }
            if(device instanceof Client){
                ((Client) device).clearConfirmation();
            }
            System.out.println("DEVICE [" + device.getId() + "]: disconnected");
        }
    }

    public void handleMessage(org.eclipse.jetty.websocket.api.Session session, String message) throws JsonSyntaxException, IOException{

        String msgType = gson.fromJson(message, JsonObject.class).get("type").getAsString();
        if( msgType.equals("HELLO") ){
            MsgHello msg = gson.fromJson(message, MsgHello.class);

            if(!dbm.isConnectedSocket(msg.getId()) && msg.getDeviceType() == 0){ // Client doesnt have fingerprint service running
                session.close(1013, "No socket connection");
                return;
            }

            dbm.removeConnectedWebDevicesAndDisconnect(msg.getId()); // One web connection at time
            dbm.addConnectedWebDevice(session, msg);
            System.out.println("DEVICE [" + msg.getId() + "]: connected");
            if(msg.getDeviceType() == 0){ // Client
                sendMessage(session, new MsgRooms(dbm.getClientRooms(msg.getId())));
                Client client = (Client) dbm.getDevice(msg.getId());
                client.setActiveRoom(null);
                client.setOffline(false);
            } else { // Speaker
                speakerManager.updateAudioState();
            }
            speakerManager.updateSpeakerList();
        } else { 
            Device connected = dbm.getConnectedWebDevice(session);
            if(connected != null && connected instanceof Client) {
                Client client = (Client)dbm.getDevice(connected.getId());
                switch(msgType){
                    case "PLAY": // Client want to play
                        //start sending fingerprints again
                        MsgPlay msgPlay = gson.fromJson(message, MsgPlay.class);
                        System.out.println("DEBUG: Start play");
                        musicManager.playSong(msgPlay.getSongId(), msgPlay.getFromTimeSec());
                        break;
                    case "PAUSE": // Client want to pause
                        System.out.println("DEBUG: Stop play");
                        musicManager.pauseCurrentSong();
                        break;
                    case "STOP": // Client want to stop
                        System.out.println("DEBUG: pause play");
                        musicManager.stopCurrentSong();
                        break;
                    case "NEXT": // Client want next song
                        musicManager.nextSong();
                        break;
                    case "PREV": // Client want prev song
                        musicManager.prevSong();
                        break;
                    case "ROOMS_REQUEST":
                        List<Room> rooms = dbm.getClientRooms(connected.getId());
                        if(rooms != null)
                            sendMessage(session, new MsgRooms(rooms));
                        else
                            sendMessage(session, new MsgRooms(new ArrayList<Room>()));
                        break;
                    case "CREATE_ROOM":
                        MsgCreateRoom msgCreateRoom = gson.fromJson(message, MsgCreateRoom.class);
                        dbm.deleteClientRoom(connected.getId(), msgCreateRoom.getRoomId());
                        dbm.setClientRoom(connected.getId(), msgCreateRoom.getRoomId());
                        // Update client rooms
                        sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                        break;
                    case "DELETE_ROOM":
                        MsgDeleteRoom msgDeleteRoom = gson.fromJson(message, MsgDeleteRoom.class);
                        System.out.println("DEBUG: Delete room");
                        dbm.deleteClientRoom(connected.getId(), msgDeleteRoom.getRoomId());
                        // Update client rooms
                        sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                        break;
                    case "ROOM_URL":
                        MsgRoomURL msgRoomURL = gson.fromJson(message, MsgRoomURL.class);
                        dbm.setRoomUrls(connected.getId(), msgRoomURL.getRoomId(), 
                                        msgRoomURL.getUrlEnter(), msgRoomURL.getUrlLeave());
                        // Update client rooms
                        sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                        break;
                    case "SCAN_ROOM":
                        MsgScanRoom msgScanRoom = gson.fromJson(message, MsgScanRoom.class);
                        if(msgScanRoom.getRoomId() != null){
                            System.out.println("DEBUG: START scan");
                            client.setActiveRoom(msgScanRoom.getRoomId().toLowerCase());
                            client.setOffline(true);
                        } else {
                            System.out.println("DEBUG: STOP scan");
                            client.setActiveRoom(null);
                            client.setOffline(false);
                        }
                        // Update client rooms
                        sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                        break;
                    case "BIND_SPEAKER":
                        MsgBindSpeaker msgBindSpeaker = gson.fromJson(message, MsgBindSpeaker.class);
                        if(msgBindSpeaker.getRoomId() != null)
                            dbm.bindSpeaker(connected.getId(), msgBindSpeaker.getSpeakerId(), msgBindSpeaker.getRoomId());
                        else
                            dbm.unbindSpeaker(connected.getId(), msgBindSpeaker.getSpeakerId());
                        // Update rooms
                        sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                        break;
                    case "CONFIRMATION_ROOM":
                        MsgConfirmationRoom msgConfirmationRoom = gson.fromJson(message, MsgConfirmationRoom.class);
                        if(msgConfirmationRoom.getRoomId() != null)
                            dbm.updateRoomFingerprintsConfirm(client, msgConfirmationRoom.getRoomId());
                        else
                            client.clearConfirmation();
                        break;
                    case "SETTINGS":
                        MsgSettings msgSettings = gson.fromJson(message, MsgSettings.class);
                        msgSettings.saveSettings();
                        break;
                    case "GET_SETTINGS":
                        sendMessage(session, new MsgSettings());
                        break;             
                }
            }
        }
    }

    public static void sendMessage(Session session, Msg message) throws IOException{
        synchronized (session) {
            session.getRemote().sendString( gson.toJson(message) );
        }
    }
}
