package it.unibo.sca.multiroomaudio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import it.unibo.sca.multiroomaudio.server_pkg.*;
import it.unibo.sca.multiroomaudio.shared.IPFinder;

public class ServerMain {

    private final static int servport = 8497;
    public static void main(String[] args){
        Thread threadexec = new ClientExecutor();
        Thread multicastThread = new BroadcastThread();
        
        multicastThread.start();
        
        
        threadexec.start();
        try{
            ServerSocket serverSocket = new ServerSocket(servport);
            System.out.println("In attesa di connessione...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted a connection");
                try{
                    ((ClientExecutor) threadexec).getRequestQ().put(new ConnThread(clientSocket));
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
