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
        DiscoveryService discoverService = new DiscoveryService();
        if(!discoverService.discover()) return;
        
        //create socket for the fingerprints     
        Socket socket = null;
        try {
            socket = new Socket(discoverService.getServerAddress(), discoverService.getFingerprintPort());
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            dOut.writeUTF(gson.toJson(new MsgHello(0, discoverService.getMac(), "Francesco"))); // TODO cambiare nome
            String json = dIn.readUTF();
            msg = gson.fromJson(json, MsgHelloBack.class);
            
        } catch(IOException e) {
            e.printStackTrace();
        }

        if(!msg.isRejected()) {
            (new FingerprintService(socket)).start();

            int wPort = discoverService.getWebServerPort();
            int mPort = discoverService.getMusicServerPort();
            String uriString = "http://" + discoverService.getServerAddress().getHostAddress() + ":" + wPort
                                        + "?type=client&id=" + msg.getClientId() + "&wPort=" + wPort + "&mPort=" + mPort;
            URI uri;
            try {
                uri = new URI(uriString);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Thread.sleep(2000);
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException | InterruptedException e) {}
                }
            } catch (URISyntaxException e) {}
    
            System.out.println("If you are not redirected visit: " + uriString);

        } else {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Connection rejected");
            }
        }
    }

}