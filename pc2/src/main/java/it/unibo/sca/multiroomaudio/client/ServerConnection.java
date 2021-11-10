package it.unibo.sca.multiroomaudio.client;

import java.net.URI;
import java.nio.ByteBuffer;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;


public class ServerConnection extends WebSocketClient {
    Gson gson = new Gson();
    
    public ServerConnection(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public ServerConnection(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection opened");
        send(new MsgHello(0, "ciao").toJson(gson));
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

}