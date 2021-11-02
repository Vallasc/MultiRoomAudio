package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import it.unibo.sca.multiroomaudio.shared.Pair;

public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast
        Pair<Integer, InetAddress> pair = DiscoveryService.discover();
        if(pair == null)
            return;
        InetAddress serverAddr = (InetAddress) pair.getV();
        int port = (Integer) pair.getU();
        System.out.println(port);
        // Start socket for web-client conection  
        port(8085);
        webSocket("/", ConnectionWebSocket.class);
        init();

        // Open web-interface
        System.out.println("Open: " + "http://google.com");
        if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("http://google.com"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        (new FingerprintService()).run();
    }

}