package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;

import it.unibo.sca.multiroomaudio.server.http_server.HttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.MusicHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.ServerWebSocket;

public class ServerMain {

    private final static int servport = 8497;
    public static void main(String[] args){
        // Music http server
        try {
            if(args.length == 2) {
                new MusicHttpServer(8080, args[1]).listMusic().start();
            } else {
                new MusicHttpServer(8080, "C:\\Users\\giaco\\Music").listMusic().start(); //TODO
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // WebApp http server
        new HttpServer(80).setWebSocket(ServerWebSocket.class).start();

        //Thread tcpHandler = new ClientExecutor();
        Thread udpHandler = new DatagramThread();
        
        udpHandler.start();        
        /*tcpHandler.start();
        try(ServerSocket serverSocket = new ServerSocket(servport)){
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
		}*/
        
    }
   

}
