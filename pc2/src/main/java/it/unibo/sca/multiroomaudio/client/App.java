package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast

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