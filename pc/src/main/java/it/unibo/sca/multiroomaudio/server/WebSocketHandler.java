package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.dto.Client;
import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.dto.Speaker;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPlay;

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
                if( msgType.equals("PLAY") ){ // Client want to play
                    MsgPlay msg = gson.fromJson(message, MsgPlay.class);
                    musicManager.playSong(msg.getSongId(), msg.getFromTimeSec());
                } else if( msgType.equals("PAUSE") ){ // Client want to pause
                    musicManager.pauseCurrentSong();
                } else if( msgType.equals("STOP") ){ // Client want to stop
                    musicManager.stopCurrentSong();
                } else if( msgType.equals("NEXT") ){ // Client want next song
                    musicManager.nextSong();
                } else if( msgType.equals("PREV") ){ // Client want next song
                    musicManager.prevSong();
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
