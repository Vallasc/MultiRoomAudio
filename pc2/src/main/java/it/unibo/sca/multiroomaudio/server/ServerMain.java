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
        DatabaseManager dbm = new DatabaseManager();
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
        new HttpServer(80, dbm).setWebSocket(ServerWebSocket.class).start();

        //Thread tcpHandler = new ClientExecutor();
        Thread udpHandler = new DatagramThread();

        udpHandler.start();        
        try(ServerSocket serverSocket = new ServerSocket(servport)){
            //only one connection at a time is accepted through the socket, that's the client, speakers are handled through websockets exclusively
            System.out.println("Waiting for connection...");
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("accepted a connection");
                (new SocketHandler(clientSocket)).run();
            }
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
        
    }
   

}
