package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.Pair;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;



public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast
        Gson gson = new Gson();
        Pair<Integer, InetAddress> pair = null;
        
        pair = DiscoveryService.discover();
        
        if((Integer) pair.getU() == -1 && pair.getV() == null){
            System.out.println("Discovery failed, closing the client");
            return;
        }
        InetAddress serverAddr = (InetAddress) pair.getV();
        int port = (Integer) pair.getU();
        // Start socket for web-client conection  
        /*
        // Open web-interface
        /*if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("http://google.com"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println("WebSocket connection");
        URI uri = null;
        try {
            uri = new URI("ws://"+serverAddr.getHostAddress()+"/websocket");
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        ServerConnection sc = new ServerConnection(uri);
        try {
            sc.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("connecting to the server through a socket");
        Socket socket = null;
        try{            
            socket = new Socket(serverAddr, port);    
        }catch(IOException e){
            System.err.println("Error in creating the socket");
            e.printStackTrace();
        }
        //this is the online fingerprint that runs non stop
        (new FingerprintService(socket)).start();
        
        
    }

}