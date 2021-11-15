package it.unibo.sca.multiroomaudio.client;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import it.unibo.sca.multiroomaudio.shared.messages.*;


public class ServerConnection extends WebSocketClient {
    
    static final Gson gson = new Gson();
    final String macAddr;
    final int type;  
    
    public ServerConnection(URI serverUri, Draft draft) {
        super(serverUri, draft);
        this.macAddr = null;
        this.type = -1;
    }

    public ServerConnection(URI serverURI) {
        super(serverURI);
        this.macAddr = null;
        this.type = -1;
    }

    public ServerConnection(URI serverURI, String macAddr, int type) {
        super(serverURI);
        this.macAddr = macAddr;
        this.type = type;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection opened");
        send(new MsgHello(type, macAddr).toJson(gson));
        
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        //this should be alike the one with the server but with the message from the client (w/o session)
        try {
            handleMessage(message, gson.fromJson(message, JsonObject.class).get("type").getAsString());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error with the message: " + message );
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    //returns the code of the results (http like)
    private void handleMessage(String message, String type) throws IOException {
        switch (type) {
            case "HELLO_BACK":
                MsgHelloBack helloBack = gson.fromJson(message, MsgHelloBack.class);
                //dovrei salvare cose nell'hashmap
                handleHelloBack(helloBack);
                
                break;
            case "REJECTED":
                MsgReject reject = gson.fromJson(message, MsgReject.class);
                handleReject(reject);
                break;
            default:
                System.out.println("MESSAGE UNKNOWN: " + message);
                break;

        }
    }


    private void handleHelloBack(MsgHelloBack hello) throws IOException{
        System.out.println("WS connection established");
    }

    private void handleReject(MsgReject reject) throws IOException{
        System.out.println("Server refused connection; reason: " + reject.getReason());
        
    }

}