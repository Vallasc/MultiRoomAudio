package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ClientMain {
    
    public static void main(String[] args) {
        
        // Find ip and port with broadcast
        Gson gson = new Gson();
        MsgHelloBack msg = null;
        DiscoveryService discovered = new DiscoveryService();
        //create socket for the fingerprints     
        Socket socket = null;

        try {
            socket = new Socket(discovered.getServerAddress(), discovered.getFingerprintPort());
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            dOut.writeUTF(gson.toJson(new MsgHello(0, discovered.getMac(), "Francesco"))); // TODO cambiare nome
            String json = dIn.readUTF();
            msg = gson.fromJson(json, MsgHelloBack.class);
            
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        if (Desktop.isDesktopSupported()) {
            try {
                if (Runtime.getRuntime().exec(new String[] { "which", "xdg-open" }).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[] {"xdg-open", "http://"+discovered.getServerAddress().getHostAddress()+":"+discovered.getServerPort()+"?"+msg.getCompletePath()});
                }
                /*Desktop.getDesktop().browse(
                    new URI("http://"+discovered.getServerAddress().getHostAddress()+":"+discovered.getServerPort()+"?"+msg.getCompletePath()));*/
            } catch (IOException /*| URISyntaxException*/ e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Open " + "http://"+discovered.getServerAddress().getHostAddress()+":"+discovered.getServerPort()+"?"+msg.getCompletePath());
        }
        if(!msg.getPath().equals("type=rejected")) {
            (new FingerprintService(socket)).start();
        } else {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}