package it.unibo.sca.multiroomaudio.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import java.awt.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.messages.*;



public class App {
    
    public static void main(String[] args) {
        
        Gson gson = new Gson();
        // Find ip and port with broadcast      
        DiscoveryService discovered = new DiscoveryService();
        
        if(discovered.getFailed()){
            System.out.println("Discovery failed, closing the client");
            return;
        }
        // Start socket for web-client conection  
        
        // Open web-interface
        if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("http://" + discovered.getServerAddress().getHostAddress() + "?type=client"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        System.out.println("WebSocket connection");
        URI uri = null;
        int type = 0;
        try {
            uri = new URI("ws://"+discovered.getServerAddress().getHostAddress()+"/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Socket socket = null;
        try{            
            socket = new Socket(discovered.getServerAddress(), discovered.getPort());    
        }catch(IOException e){
            System.err.println("Error in creating the socket");
            e.printStackTrace();
        }
        try{
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            dOut.writeUTF(new MsgHello(type, discovered.getMac(), true).toJson(gson));
            String msg = dIn.readUTF();
            if(gson.fromJson(msg, JsonObject.class).get("type").getAsString().equals("REJECTED")){
                socket.close();
                MsgReject reject = gson.fromJson(msg, MsgReject.class);
                System.out.println(reject.getReason());
                return;
            }
        }catch(IOException e){
            System.err.println("Error during the socket handshake phase");
            e.printStackTrace();
        }
        ServerConnection sc = new ServerConnection(uri, discovered.getMac(), type);
        try {
            sc.connectBlocking();
        } catch (InterruptedException e) {
            sc.close();
            try{
                socket.close();
            }catch(IOException e1){}
            e.printStackTrace();
        }
        //this is the online fingerprint that runs non stop
        (new FingerprintService(socket)).start();
       
        //sc.close();
        
    }


    

}