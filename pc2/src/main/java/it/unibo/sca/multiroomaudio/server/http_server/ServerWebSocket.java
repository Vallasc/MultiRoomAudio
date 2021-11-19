package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.messages.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@WebSocket
public class ServerWebSocket {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static final Gson gson = new Gson();
    private final DatabaseManager dbm;  

    public ServerWebSocket(DatabaseManager dbm){
        this.dbm = dbm;
    }

    @OnWebSocketConnect
    public void connected(Session session) {
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        try{
            dbm.connectedDevices.remove(dbm.getKeyDevice(session.getRemoteAddress().getHostString()));
        }catch(NullPointerException e){
            System.out.println("already removed");
        }
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        handleMessage(session, message);
    }
    public void handleMessage(Session session, String message) throws JsonSyntaxException, IOException{

        if(gson.fromJson(message, JsonObject.class).get("type").getAsString().equals("HELLO")){
            handleHello(session, gson.fromJson(message, MsgHello.class));
        }else if(gson.fromJson(message, JsonObject.class).get("type").getAsString().equals("CLOSE")){
            handleClose(gson.fromJson(message, MsgClose.class));
        }
    }

    @OnWebSocketError
    public void throwError(Throwable error) {
        System.out.println(error.getMessage());
    }

    public void handleHello(Session session, MsgHello hello) throws IOException{
        if(hello.getDeviceType() == 0){
            //client
            if(dbm.connectedDevices.putIfAbsent(hello.getIp(), true) == null)
                session.getRemote().sendString(gson.toJson(new MsgHelloBack("REJECTED")));
            else{//insert insipe connected device
                dbm.devices.putIfAbsent(hello.getIp(), new Device(hello.getDeviceType(), hello.getIp()));
                session.getRemote().sendString(gson.toJson(new MsgHelloBack()));
                sessions.add(session);
            }
        }
    }

    public void handleClose(MsgClose close){
        dbm.connectedDevices.remove(close.getIp());
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