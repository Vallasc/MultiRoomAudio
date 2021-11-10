package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.google.gson.Gson;

@WebSocket
public class ServerWebSocket {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static final Gson gson = new Gson();

    @OnWebSocketConnect
    public void connected(Session session) {
        System.out.println("connected");
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        System.out.println("closed");
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        
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