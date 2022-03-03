package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.prefs.Preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ClientMain {

    static class ShutDownHandler extends Thread {
        private final String id;
        public ShutDownHandler(String id){
            this.id = id;
        }
        public void run() {
            try (Writer writer = new FileWriter(".\\pc\\src\\main\\java\\it\\unibo\\sca\\multiroomaudio\\client\\id.json")) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(this.id, writer);
            }catch(IOException e){
                System.err.println("error while serializing the devices file");
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        
        // Find ip and port with broadcast
        Gson gson = new Gson();
        MsgHelloBack msg = null;
        DiscoveryService discoverService = new DiscoveryService();
        if(!discoverService.discover()) return;
        String id = null;
        
        //create socket for the fingerprints     
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


        if(!msg.isRejected()) {
            (new FingerprintService(socket)).start();
            try {
                JsonReader reader = new JsonReader(new FileReader(".\\pc\\src\\main\\java\\it\\unibo\\sca\\multiroomaudio\\client\\id.json"));
                id = gson.fromJson(reader, String.class);
            } catch (FileNotFoundException e1) {};

            if(id == null)
                id =  msg.getClientId();
            System.out.println(id);
            Runtime.getRuntime().addShutdownHook(new ShutDownHandler(id));

            int wPort = discoverService.getWebServerPort();
            int mPort = discoverService.getMusicServerPort();
            String uriString = "http://" + discoverService.getServerAddress().getHostAddress() + ":" + wPort
                                        + "?type=client&id=" + id + "&wPort=" + wPort + "&mPort=" + mPort;
            URI uri;
            try {
                uri = new URI(uriString);
                if (Desktop.isDesktopSupported()) {
                    try {
                        Thread.sleep(2000);
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException | InterruptedException | UnsupportedOperationException e) {
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