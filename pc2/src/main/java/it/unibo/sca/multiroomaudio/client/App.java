package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;


import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;



public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast
        Gson gson = new Gson();
        
        DiscoveryService discovered = new DiscoveryService();
        
        if(discovered.getFailed()){
            System.out.println("Discovery failed, closing the client");
            return;
        }
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
        int type = 0;
        try {
            uri = new URI("ws://"+discovered.getServerAddress().getHostAddress()+"/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ServerConnection sc = new ServerConnection(uri, discovered.getMac(), type);
        try {
            sc.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("connecting to the server through a socket");
        Socket socket = null;
        try{            
            socket = new Socket(discovered.getServerAddress(), discovered.getPort());    
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeUTF(new MsgHello(type, discovered.getMac(), true).toJson(gson));

        }catch(IOException e){
            System.err.println("Error in creating the socket");
            e.printStackTrace();
        }
        //this is the online fingerprint that runs non stop
        //(new FingerprintService(socket)).start();
       
        //sc.close();
        
    }

}