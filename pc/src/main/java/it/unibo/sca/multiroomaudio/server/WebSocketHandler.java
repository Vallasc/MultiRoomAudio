package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPlay;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgBindSpeaker;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgCreateRoom;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgDeleteRoom;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgRooms;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgScanRoom;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Device;
import it.unibo.sca.multiroomaudio.shared.model.Room;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final DatabaseManager dbm;  
    private final MusicOrchestrationManager musicManager;
    private final SpeakerManager speakerManager;
    ExecutorService pool = Executors.newFixedThreadPool(4);

    public WebSocketHandler(DatabaseManager dbm, MusicOrchestrationManager musicManager, SpeakerManager speakerManager) {
        this.dbm = dbm;
        this.musicManager = musicManager;
        this.speakerManager = speakerManager;
    }

    public void handleClose(Session session){
        Device device = dbm.removeConnectedWebDevice(session);
        if(device != null){
            if(device instanceof Client){
                dbm.removeConnectedSocketClient(device.getId());
            }
            if(device instanceof Speaker){
                speakerManager.updateSpeakerList();
            }
            System.out.println("Device [" + device.getId() + "] disconnected");
        }
    }

    
    public void handleMessage(org.eclipse.jetty.websocket.api.Session session, String message) throws JsonSyntaxException, IOException{

        String msgType = gson.fromJson(message, JsonObject.class).get("type").getAsString();
        if( msgType.equals("HELLO") ){
            MsgHello msg = gson.fromJson(message, MsgHello.class);
            dbm.addConnectedWebDevice(session, msg);
            System.out.println("Device [" + msg.getId() + "] connected");
            speakerManager.updateSpeakerList();
        } else{ 
            Device connected = dbm.getConnectedWebDevice(session);
            if(connected != null && connected instanceof Client) {
                Client client = (Client)dbm.getDevice(connected.getId());
                if( msgType.equals("PLAY") ){ // Client want to play
                    MsgPlay msg = gson.fromJson(message, MsgPlay.class);
                    System.out.println("DEBUG: Start play");
                    pool.execute(new MinimizeRSSErr(speakerManager, client, dbm, false));
                    musicManager.playSong(msg.getSongId(), msg.getFromTimeSec());
                } else if( msgType.equals("PAUSE") ){ // Client want to pause
                    System.out.println("DEBUG: Stop play");
                    client.setPlay(false);
                    musicManager.pauseCurrentSong();
                } else if( msgType.equals("STOP") ){ // Client want to stop
                    System.out.println("DEBUG: pause play");
                    client.setPlay(false);
                    musicManager.stopCurrentSong();
                } else if( msgType.equals("NEXT") ){ // Client want next song
                    musicManager.nextSong();
                } else if( msgType.equals("PREV") ){ // Client want prev song
                    musicManager.prevSong();
                } else if( msgType.equals("ROOMS_REQUEST")){
                    List<Room> rooms = dbm.getClientRooms(connected.getId());
                    if(rooms != null)
                        sendMessage(session, new MsgRooms(rooms));
                    else
                        sendMessage(session, new MsgRooms(new ArrayList<Room>()));
                } else if( msgType.equals("CREATE_ROOM")){
                    MsgCreateRoom msg = gson.fromJson(message, MsgCreateRoom.class);
                    dbm.deleteClientRoom(connected.getId(), msg.getRoomId());
                    dbm.setClientRoom(connected.getId(), msg.getRoomId());
                    sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                } else if( msgType.equals("DELETE_ROOM")){
                    MsgDeleteRoom msg = gson.fromJson(message, MsgDeleteRoom.class);
                    System.out.println("DEBUG: Delete room");
                    dbm.deleteClientRoom(connected.getId(), msg.getRoomId());
                    sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                } else if( msgType.equals("SCAN_ROOM")){
                    MsgScanRoom msg = gson.fromJson(message, MsgScanRoom.class);
                    if(msg.getStartScan()){
                        System.out.println("DEBUG: Start scan");
                        dbm.setDeviceStart(connected.getId(), msg.getRoomId(), msg.getNScan());
                    } else {
                        System.out.println("DEBUG: Stop scan");
                        dbm.setDeviceStop(connected.getId(), msg.getNScan());
                        sendMessage(session, new MsgRooms(dbm.getClientRooms(connected.getId())));
                    }
                } else if( msgType.equals("BIND_SPEAKER")){
                    MsgBindSpeaker msg = gson.fromJson(message, MsgBindSpeaker.class);
                    Speaker speaker = dbm.getConnectedSpeaker(msg.getSpeakerId());
                    speaker.setRoom(msg.getRoomId());
                    System.out.println("DEBUG: bind speaker\n" + speaker.getRoom());
                    //check if the speaker is already bound to another room 
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
