package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPause;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPlay;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgStop;

public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final DatabaseManager dbm;  
    private MusicOrchestrationManager musicManager;


    public WebSocketHandler(DatabaseManager dbm, MusicOrchestrationManager musicManager) {
        this.dbm = dbm;
        this.musicManager = musicManager;
    }

    public void handleClose(Session session){
        String deviceId = dbm.removeConnectedWebDevice(session);
        if(deviceId != null){
            dbm.removeConnectedSocketClient(deviceId); // If it is not a clint does nothing
            System.out.println("Device [" + deviceId + "] disconnected");
        }
    }

    
    public void handleMessage(org.eclipse.jetty.websocket.api.Session session, String message) throws JsonSyntaxException, IOException{

        String msgType = gson.fromJson(message, JsonObject.class).get("type").getAsString();
        if( msgType.equals("HELLO") ){
            MsgHello msg = gson.fromJson(message, MsgHello.class);
            dbm.addConnectedWebDevice(session, msg);
            System.out.println("Device [" + msg.getId() + "] connected");
        } else if( msgType.equals("PLAY") ){ // Client want to play TODO controllare che sia effettivamente il client
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

    public static void sendMessage(Session session, Msg message) throws IOException{
        session.getRemote().sendString( gson.toJson(message) );
    }
}
