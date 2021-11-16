package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;


public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast
        
        DiscoveryService discovered = new DiscoveryService();
        // Start socket for web-client conection  


        if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("http://"+discovered.getServerAddress().getHostAddress()+""));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        try(Socket socket = new Socket(discovered.getServerAddress(), discovered.getPort())){
            (new FingerprintService(socket)).run();
        }catch(IOException e){
            System.err.println("Error in creating the socket");
            e.printStackTrace();
        }
        
    }

}