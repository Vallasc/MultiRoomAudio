package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MyMsgHandler;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHelloBack;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebSocket
public class ServerWebSocket {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<String, Session> devicesMap = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    
    @OnWebSocketConnect
    public void connected(Session session) {
        System.out.println("connected");
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        devicesMap.remove("");//todo, unless the add isn't just an edit after the first connection
        sessions.remove(session);
        System.out.println("closed");
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        handleMessage(session, message, gson.fromJson(message, JsonObject.class).get("type").getAsString());
        
        
    }

    public static void handleMessage(Session session, String message, String type){
        switch (type) {
            case "HELLO":
                System.out.println("received an hello");
                MsgHello hello = gson.fromJson(message, MsgHello.class);
                //dovrei salvare cose nell'hashmap
                handleHello(session, hello);
                break;
            default:
                System.out.println("MESSAGE UNKNOWN ");
            break;
        }
    }

    public static void handleHello(Session session, MsgHello hello){
        sessions.forEach((s) -> {
            if(s.getRemote().equals(session.getRemote())){
                //don't know if needed
                devicesMap.put(hello.getId(), session);
                return;
            }
        });
        try {
            session.getRemote().sendString(gson.toJson(new MsgHelloBack()));
        } catch (IOException e) {
            System.err.println(e.toString());
        }
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