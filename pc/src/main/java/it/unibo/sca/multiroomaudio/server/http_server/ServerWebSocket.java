package it.unibo.sca.multiroomaudio.server.http_server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import it.unibo.sca.multiroomaudio.server.WebSocketHandler;

import java.io.*;

@WebSocket
public class ServerWebSocket {

    private final WebSocketHandler webSocketHandler;

    public ServerWebSocket(WebSocketHandler webSocketHandler){
        this.webSocketHandler = webSocketHandler;
    }

    @OnWebSocketConnect
    public void connected(Session session) {
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        webSocketHandler.handleMessage(session, message);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        webSocketHandler.handleClose(session);                
    }

    @OnWebSocketError
    public void throwError(Throwable error) {
        //System.out.println("Web socket error " + error.getMessage() );
    }

}