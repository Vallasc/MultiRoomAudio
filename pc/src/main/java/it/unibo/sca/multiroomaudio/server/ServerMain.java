package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import it.unibo.sca.multiroomaudio.server.http_server.HttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.MusicHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.ServerWebSocket;
public class ServerMain {

    private final static int servport = 8497;
    public static void main(String[] args){
        
        // Music http server
        DatabaseManager dbm = new DatabaseManager();
        Thread udpHandler = new DatagramThread();
        
        udpHandler.start();  
        try {
            if(args.length >= 1) {
                new MusicHttpServer(8080, args[0]).listMusic().start();
            } else {
                new MusicHttpServer(8080, "/home/vallasc/Musica").listMusic().start(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // WebApp http server
        new HttpServer(80).setWebSocket(new ServerWebSocket(dbm)).start();      
        //(new FingerprintAnalyzer(dbm)).start();
        try(ServerSocket serverSocket = new ServerSocket(servport)){
            //only one connection at a time is accepted through the socket, that's the client, speakers are handled through websockets
            while(true){
                Socket clientSocket = serverSocket.accept();
                (new SocketHandler(clientSocket, dbm)).start();
            }
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
        
    }
   

}
