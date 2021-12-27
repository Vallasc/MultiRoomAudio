package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import it.unibo.sca.multiroomaudio.server.http_server.MainHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.MusicHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.ServerWebSocket;
public class ServerMain {

    private final static int servport = 8497;
    public static void main(String[] args){
                
        DatabaseManager dbm = new DatabaseManager();
        Thread udpHandler = new DatagramThread();
        udpHandler.start();
        
        SpeakerManager speakerManger = new SpeakerManager(dbm);
        MusicOrchestrationManager musicManager = new MusicOrchestrationManager(dbm);
        musicManager.start();

        // Music http server
        try {
            if(args.length >= 1) {
                new MusicHttpServer(8080, args[0], musicManager).listMusic().start();
            } else {
                new MusicHttpServer(8080, "/home/francesco/Music", musicManager).listMusic().start();
                //new MusicHttpServer(8080, "C:\\Users\\giaco\\Music", musicManager).listMusic().start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // WebApp http server
        new MainHttpServer(80, 
            new ServerWebSocket(
                new WebSocketHandler(dbm, musicManager, speakerManger)
                ),
            dbm ).start();      
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
