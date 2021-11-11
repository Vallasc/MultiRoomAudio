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
    
    public ServerConnection(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public ServerConnection(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connection opened");
        //this should be done with the thingy things
        
        
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);
        //this should be alike the one with the server but with the message from the client (w/o session)
        try {
            int ret = handleMessage(message, gson.fromJson(message, JsonObject.class).get("type").getAsString());
            if(ret != 200)
                close(ret);
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
    private static int handleMessage(String message, String type) throws IOException {
        switch (type) {
            case "HELLO_BACK":
                System.out.println("received an helloBack");
                MsgHelloBack helloBack = gson.fromJson(message, MsgHelloBack.class);
                //dovrei salvare cose nell'hashmap
                handleHelloBack(helloBack);
                return 200;
            case "REJECTED":
                System.out.println("received a reject");
                MsgReject reject = gson.fromJson(message, MsgReject.class);
                handleReject(reject);
                return 503;
            default:
                System.out.println("MESSAGE UNKNOWN ");
                return 500;
        }
    }

    private static void handleHelloBack(MsgHelloBack hello) throws IOException{
        //to accept a connection check the type, if a client is already connected and we're receiving a client (not listening) we 
        //abort the connection, if it's a speaker we accept it(?)   
    }

    private static void handleReject(MsgReject reject) throws IOException{
        System.out.println("Server refused connection; reason: " + reject.getReason());
    }

}