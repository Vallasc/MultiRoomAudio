package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.io.EofException;
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
        
    }


    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        //invalid sessions aren't actually added
        dbm.printConnected();
        sessions.remove(session);
        dbm.removeConnected(session);
            
        System.out.println("closed");
        
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {
        System.out.println("Got: " + message);   // Print message
        try{
            System.out.println(gson.fromJson(message, JsonObject.class).get("type").getAsString());
            handleMessage(session, message, gson.fromJson(message, JsonObject.class).get("type").getAsString());
        }catch(IOException e){
            System.err.println("Error handling message: " + message); 
            e.printStackTrace();
        }
        
    }
    //Doesn't seem like needed
    @OnWebSocketError
    public void onError(Session session, Throwable cause) {
        
        if(cause instanceof EofException){
            dbm.removeConnected(session);
            sessions.remove(session);
        }
        
            
    }


    private void handleMessage(Session session, String message, String type) throws IOException {
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

    private void handleHello(Session session, MsgHello hello) throws IOException{
        //to accept a connection check the type, if a client is already connected and we're receiving a client (not listening) we 
        //abort the connection, if it's a speaker we accept it(?)
        //if the client has the same id as another one we send him a new id to make it connect
        //alternative is to abort the connection cause of duplicate MAC_ADDR, we need a hashmap id mac maybe idk
        if(hello.getDeviceType() == 0 && dbm.isClientConnected()){
            session.getRemote().sendString(gson.toJson(new MsgReject("there is already another client connected")));
            session.close();
        }
        else if(dbm.alreadyConnected(hello.getMac())){
            session.getRemote().sendString(gson.toJson(new MsgReject("this device is already connected")));
            session.close();
        }
        else{
            dbm.putConnected(hello.getMac(), new Device(hello.getDeviceType(), session, "name", hello.getMac()));
            session.getRemote().sendString(gson.toJson(new MsgHelloBack()));
            sessions.add(session);
        }
    }

    private void handleClose(Session session, MsgClose close) throws IOException{
        dbm.removeConnected(session);
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