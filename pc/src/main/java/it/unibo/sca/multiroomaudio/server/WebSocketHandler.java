package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;

public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final DatabaseManager dbm;  


    public WebSocketHandler(DatabaseManager dbm) {
        this.dbm = dbm;
    }

    public void handleClose(Session session){
        String key = dbm.removeSessions(session);
        
        if(dbm.countSessions(key) == 0){
            dbm.removeConnectedSocketClient(key);
            dbm.setDeviceStop(key);
        }
    }

    public void handleHello(Session session, MsgHello hello) throws IOException{
        /*if(hello.getDeviceType() == 0){ // Client
            System.out.println(hello.getId());
            
            // TODO 
            //not needed anymore cause there could be more than one websocket for each client
            if(!dbm.isConnectedSocket(hello.getId())){
                System.out.println("not registered");
                dbm.addConnectedWebDevice(session, hello); // TODO fare simile a speaker sotto
            }
            else{//insert insipe connected device
                System.out.println("registered");
                dbm.addConnectedWebDevice(session, hello); // TODO fare simile a speaker sotto
            }
        } else if(hello.getDeviceType() == 1){ // Speaker
            dbm.addConnectedWebDevice(session, hello);
        }*/
        dbm.addSession(session, hello.getId());

        dbm.addConnectedWebDevice(session, hello);
    }
    
    public void handleMessage(org.eclipse.jetty.websocket.api.Session session, String message) throws JsonSyntaxException, IOException{

        String msgType = gson.fromJson(message, JsonObject.class).get("type").getAsString();
        if( msgType.equals("HELLO") ){
            MsgHello msg = gson.fromJson(message, MsgHello.class);
            handleHello(session, msg);
        }
    }
}
