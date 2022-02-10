package it.unibo.sca.multiroomaudio.speaker;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;

public class SpeakerMain {
    
    public static void main(String[] args) {
        
        DiscoveryService discovered = new DiscoveryService();

        String uriString = "http://"+discovered.getServerAddress().getHostAddress()+":"+discovered.getServerPort()+"?type=speaker";
        URI uri;
        try {
            uri = new URI(uriString);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (Exception e) {
                    it.unibo.sca.multiroomaudio.utils.Desktop.browse(uri);
                }
            }
        } catch (URISyntaxException e1) {}

        System.out.println("If you are not redirected visit: " + uriString);
    }

}