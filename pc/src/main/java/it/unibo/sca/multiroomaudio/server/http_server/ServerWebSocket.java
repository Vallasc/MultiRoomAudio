package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import it.unibo.sca.multiroomaudio.server.WebSocketHandler;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@WebSocket
public class ServerWebSocket {
    private final Queue<Session> sessions = new ConcurrentLinkedQueue<>(); // TODO In futuro andra tolto

    private final WebSocketHandler webSocketHandler;

    public ServerWebSocket(WebSocketHandler webSocketHandler){
        this.webSocketHandler = webSocketHandler;
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
    }


    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        //System.out.println("Got: " + message);   // Print message
        webSocketHandler.handleMessage(session, message);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        /*try{
            dbm.removeConnectedSocketDevice(dbm.getKeyDevice(session.getRemoteAddress().getHostString()));
        }catch(NullPointerException e){
            System.out.println("already removed");
        }*/
        sessions.remove(session);

        webSocketHandler.handleClose(session);
        
        System.out.println("closed " + statusCode + " " +reason );
        
    }

    @OnWebSocketError
    public void throwError(Throwable error) {
        System.out.println(error.getMessage());
    }

    /*synchronized public static void sendAll(Msg message){
        sessions.forEach((session) -> {
            try {
                session.getRemote().sendString( gson.toJson(message) );
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        });
    }*/

}