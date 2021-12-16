package it.unibo.sca.multiroomaudio.speaker;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;

public class SpeakerMain {
    
    public static void main(String[] args) {
        
        DiscoveryService discovered = new DiscoveryService();

        if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(
                    new URI("http://"+discovered.getServerAddress().getHostAddress()+":"+discovered.getServerPort()+"?type=speaker"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Open " + "http://"+discovered.getServerAddress().getHostAddress()+":"+discovered.getServerPort()+"?type=speaker");
        }
        
    }

}