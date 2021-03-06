package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.prefs.Preferences;

import com.google.gson.Gson;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.messages.*;
import it.unibo.sca.multiroomaudio.utils.Utils;

/**
 * Main client class
 */
public class ClientMain {

    public static void main(String[] args) {
        
        // Find ip and port with broadcast
        Gson gson = new Gson();
        MsgHelloBack msg = null;
        DiscoveryService discoverService = new DiscoveryService();
        if(!discoverService.discover()) return;
        String id = null;
        
        // Create socket for the fingerprints     
        Socket socket = null;
        try {
            socket = new Socket(discoverService.getServerAddress(), discoverService.getFingerprintPort());
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            dOut.writeUTF(gson.toJson(new MsgHello(0, getUniqueId())));
            String json = dIn.readUTF();
            msg = gson.fromJson(json, MsgHelloBack.class);
            
        } catch(IOException e) {
            e.printStackTrace();
        }

        // If device is not rejected
        if(!msg.isRejected()) {
            (new FingerprintService(socket)).start();
            if(id == null)
                id =  msg.getClientId();
            System.out.println("ID: " + id);
            int wPort = discoverService.getWebServerPort();
            int mPort = discoverService.getMusicServerPort();
            String uriString = "http://" + discoverService.getServerAddress().getHostAddress() + ":" + wPort
                                        + "?type=client&id=" + id + "&wPort=" + wPort + "&mPort=" + mPort;
            URI uri;
            try {
                uri = new URI(uriString);
                if (Desktop.isDesktopSupported()) {
                    Utils.sleep(2000);
                    try {
                        // Open web app
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException | UnsupportedOperationException e) {
                        System.err.println("Unsupported desktop environment");
                    }
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

    /**
     * Gen a random ID and save it in preferences
     * @return ID
     */
    private static String getUniqueId(){
        Preferences prefs = Preferences.userNodeForPackage(ClientMain.class);
        String id = prefs.get("ID", null);
        if(id == null) {
            id = UUID.randomUUID().toString();
            prefs.put("ID", id);
        }
        return id;
    }
}