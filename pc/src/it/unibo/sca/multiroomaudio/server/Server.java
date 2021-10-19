package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final static int servport = 8497;
    public static void main(String[] args){
        Thread threadexec = new Executor();
        Thread multicastThread = new MulThread();
        multicastThread.run();
        

        threadexec.start();
        try{
            ServerSocket serverSocket = new ServerSocket(servport);
            System.out.println("In attesa di connessione...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                try{
                    ((Executor) threadexec).getRequestQ().put(new ConnThread(clientSocket));
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
