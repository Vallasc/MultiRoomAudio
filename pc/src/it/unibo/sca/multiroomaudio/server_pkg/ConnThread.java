package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.IOException;
import java.net.Socket;

import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ConnThread extends Thread {

    private final Socket client;

    public ConnThread(Socket client){
        super();
        this.client = client;     
    }

    public void run(){
        System.out.println("MyThread running");
        MsgSpecs specs = null;
        try {
            specs =(MsgSpecs) msgHandler.tcpInMsg(client);
        } catch (ClassNotFoundException e) {
            System.err.println("Error while trying to find the class");
            try {
                client.close();
            } catch (IOException e1) {
                System.err.println("Error in closing the socket");
                e1.printStackTrace();
            }
            return;
            
        } catch( IOException e){
            System.err.println("something fucked up the socket");
            return;
        }
        System.out.println("System type: " + specs.getDeviceType() + "System MAC address: " + specs.getMACid());
        

        try {
            client.close();
        } catch (IOException e) {
            System.err.println("Error in closing the socket");
            e.printStackTrace();
        }
    }
}
