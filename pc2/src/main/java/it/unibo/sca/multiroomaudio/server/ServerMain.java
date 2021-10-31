package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import it.unibo.sca.multiroomaudio.server.*;

public class ServerMain {

    private final static int servport = 8497;
    public static void main(String[] args){
        Thread tcpHandler = new ClientExecutor();
        Thread udpHandler = new DatagramThread();
        
        udpHandler.start();        
        tcpHandler.start();
        try{
            ServerSocket serverSocket = new ServerSocket(servport);
            System.out.println("In attesa di connessione...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted a connection");
                try{
                    ((ClientExecutor) tcpHandler).getRequestQ().put(new ConnThread(clientSocket));
                } catch (InterruptedException e) {
                    System.err.println("cannot insert connThread inside q");
                    e.printStackTrace();
                }
            }
            //serverSocket.close();
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
        
    }
   

}
