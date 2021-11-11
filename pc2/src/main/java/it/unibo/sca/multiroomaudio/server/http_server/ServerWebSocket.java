package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.messages.*;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebSocket
public class ServerWebSocket {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static DatabaseManager dbm;
    private static final Gson gson = new Gson();
    
    public ServerWebSocket(){
        dbm = null;
    }

    public ServerWebSocket(DatabaseManager dbmRef){
        dbm = dbmRef;
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        System.out.println("connected");
        /*for(String key : dbm.getConnectedDevices().keySet()){
            System.out.println(key);
        }*/
        sessions.add(session);
    }


    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
        
        System.out.println("closed");
        
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        dbm.printConnected();
        System.out.println("Got: " + message);   // Print message
        try{
            handleMessage(session, message, gson.fromJson(message, JsonObject.class).get("type").getAsString());
        }catch(IOException e){
            System.err.println("Error handling message: " + message); 
            e.printStackTrace();
        }
        
    }

    private static void handleMessage(Session session, String message, String type) throws IOException {
        switch (type) {
            case "HELLO":
                System.out.println("received an hello");
                MsgHello hello = gson.fromJson(message, MsgHello.class);
                //dovrei salvare cose nell'hashmap
            
                handleHello(session, hello);
            
                break;
            case "CLOSE":
                System.out.println("received a close message");
                MsgClose close = gson.fromJson(message, MsgClose.class);
                handleClose(session, close);
                break;
            default:
                System.out.println("MESSAGE UNKNOWN ");
            break;
        }
    }

    private static void handleHello(Session session, MsgHello hello) throws IOException{
        //to accept a connection check the type, if a client is already connected and we're receiving a client (not listening) we 
        //abort the connection, if it's a speaker we accept it(?)
        if(hello.getDeviceType() == 0){
            if(dbm.isClientConnected())
                session.getRemote().sendString(gson.toJson(new MsgReject("there's already another client connected")));
            else{
                //add the connected device to the map
                dbm.putConnected(hello.getId(), new Device(hello.getDeviceType(), session, "name", hello.getId()));
                session.getRemote().sendString(gson.toJson(new MsgHelloBack()));
            }
            dbm.printConnected();
        }else{

        }   
    }

    private static void handleClose(Session session, MsgClose close) throws IOException{
        dbm.removeConnected(close.getId());
    }

    /*
    @SuppressWarnings("unchecked")
    public static <T extends Msg>T deserialize(String msg, JsonObject json){
        if(json.get("type").getAsString().equals("HELLO")){
            System.out.println("Sembra andare");
            return (T) gson.fromJson(msg, MsgHello.class);
        }
        
        return (T) gson.fromJson(msg, Msg.class);
    }*/

    public DatabaseManager getDBM(){
        return dbm;
    }

    synchronized public static void sendAll(Msg message){
        sessions.forEach((session) -> {
            try {
                session.getRemote().sendString( gson.toJson(message) );
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        });
    }

}