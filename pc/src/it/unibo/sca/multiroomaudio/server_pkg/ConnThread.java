package it.unibo.sca.multiroomaudio.server_pkg;

import java.net.Socket;

public class ConnThread extends Thread {

    private final Socket client;

    public ConnThread(Socket client){
        super();
        this.client = client;     
    }

    public void run(){
        System.out.println("MyThread running");
        System.out.println(client.getPort());
    }
}
