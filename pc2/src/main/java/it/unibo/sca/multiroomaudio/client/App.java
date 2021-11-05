package it.unibo.sca.multiroomaudio.client;
import static spark.Spark.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import it.unibo.sca.multiroomaudio.shared.Pair;
import it.unibo.sca.multiroomaudio.shared.exceptions.UknowknBroadcastException;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast
        Pair<Integer, InetAddress> pair = null;
        try{
            pair = DiscoveryService.discover();
        }catch(UknowknBroadcastException e){
            e.printMessage();
        }
        if(pair == null){
            System.out.println("Pair is null");
            return;
        }
        InetAddress serverAddr = (InetAddress) pair.getV();
        int port = (Integer) pair.getU();
        // Start socket for web-client conection  
        port(port);
        webSocket("/", ConnectionWebSocket.class);
        init();

        // Open web-interface
        System.out.println("Open: " + serverAddr.toString());
        if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("http:/"+serverAddr.toString()+":80"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        (new FingerprintService()).run();
        }

}