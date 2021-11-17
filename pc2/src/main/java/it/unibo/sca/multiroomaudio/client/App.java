package it.unibo.sca.multiroomaudio.client;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.shared.messages.*;


public class App {
    public static void main(String[] args) {
        // Find ip and port with broadcast
        Gson gson = new Gson();
        MsgHelloBack msg = null;
        DiscoveryService discovered = new DiscoveryService();
        //create socket for the fingerprints
        Socket socket = null;
        try{
            socket = new Socket(discovered.getServerAddress(), discovered.getPort());
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            dOut.writeUTF(gson.toJson(new MsgHello(0, discovered.getMac())));
            String json = dIn.readUTF();
            msg = gson.fromJson(json, MsgHelloBack.class);
            if(msg.getType().equals("REJECTED")){
                System.err.println("Connection refused");
                return;
            }
            else
                msg = gson.fromJson(json, MsgHelloBack.class);
            //
        }catch(IOException e){
            e.printStackTrace();
        }
        if(msg == null || msg.getPath() == null || msg.getPort() == -1){
            return;

        }
        if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("http://"+discovered.getServerAddress().getHostAddress()+":"+msg.getPort()+"/?"+msg.getPath()));//do we want to somehow parametrize the port too?
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        (new FingerprintService(socket)).start();
    }

}