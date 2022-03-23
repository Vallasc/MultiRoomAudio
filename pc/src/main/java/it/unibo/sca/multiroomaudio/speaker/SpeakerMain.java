package it.unibo.sca.multiroomaudio.speaker;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;

/**
 * Main speaker entry point
 */
public class SpeakerMain {
    
    public static void main(String[] args) {
        
        DiscoveryService discoverService = new DiscoveryService();
        if(!discoverService.discover()) return;

        int wPort = discoverService.getWebServerPort();
        int mPort = discoverService.getMusicServerPort();
        String uriString = "http://" + discoverService.getServerAddress().getHostAddress() + ":" + wPort
                                    + "?type=speaker" + "&wPort=" + wPort + "&mPort=" + mPort;
        URI uri;
        try {
            uri = new URI(uriString);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (Exception e) {
                    System.err.println("Unsupported desktop environment");
                }
            }
        } catch (URISyntaxException e1) {}

        System.out.println("If you are not redirected visit: " + uriString);
    }

}