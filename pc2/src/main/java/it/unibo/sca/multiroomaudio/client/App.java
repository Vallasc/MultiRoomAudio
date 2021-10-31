package it.unibo.sca.multiroomaudio.client;

import static spark.Spark.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast

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