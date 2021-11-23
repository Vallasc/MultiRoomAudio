package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.dto.Client;
import it.unibo.sca.multiroomaudio.shared.messages.MsgClose;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHelloBack;
import it.unibo.sca.multiroomaudio.shared.messages.MsgOffline;

public class WebSocketHandler {
    private static final Gson gson = new Gson();
    private final DatabaseManager dbm;  


    public WebSocketHandler(DatabaseManager dbm) {
        this.dbm = dbm;
    }

    public void handleHello(Session session, MsgHello hello) throws IOException{
        if(hello.getDeviceType() == 0){ // Client
            // TODO 
            if(!dbm.isConnectedSocket(hello.getId())){
                session.getRemote().sendString(gson.toJson(new MsgHelloBack("REJECTED"))); // TODO Non serve reject basdta che chiudi la connessione
                System.out.println("not registered");
            }
            else{//insert insipe connected device
                System.out.println("registered");
                //dbm.addConnectedWebDevice(hello.getId(), new Client(hello.getId())); // TODO fare simile a speaker sotto
                session.getRemote().sendString(gson.toJson(new MsgHelloBack())); // TODO HELLOBACK serve nel webSocket??
            }
        } else if(hello.getDeviceType() == 1){ // Speaker
            dbm.addConnectedWebDevice(session, hello);
        }
    }
    
    public void handleMessage(org.eclipse.jetty.websocket.api.Session session, String message) throws JsonSyntaxException, IOException{

        String msgType = gson.fromJson(message, JsonObject.class).get("type").getAsString();
        if( msgType.equals("HELLO") ){
            MsgHello msg = gson.fromJson(message, MsgHello.class);
            handleHello(session, msg);
        }
        
        else if( msgType.equals("CLOSE")){ // TODO la close può essere gestita direttamente dal metodo close, non serve creare un nuovo messaggio
            //handleClose(gson.fromJson(message, MsgClose.class));
        }else if( msgType.equals("OFFLINE")){
            //handleOffline(gson.fromJson(message, MsgOffline.class));
        }
    }

    public void handleClose(Session session){
        dbm.removeConnectedWebDevice(session);
    }

    public void handleOffline(MsgOffline offline){ // TODO mi sa che non serve offline
        System.out.println(offline.getStart());
        //((Client) dbm.devices.get(offline.getId())).setStart(offline.getStart()); TODO
    }
}
