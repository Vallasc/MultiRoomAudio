package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import it.unibo.sca.multiroomaudio.server.http_server.MainHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.MusicHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.ServerWebSocket;
import it.unibo.sca.multiroomaudio.server.socket_handlers.DatagramThread;
import it.unibo.sca.multiroomaudio.server.socket_handlers.SocketHandler;
import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
public class ServerMain {

    private final static int FINGERPRINT_SERVER_PORT = 8497;
    private final static int WEB_SERVER_PORT = 8082;
    private final static int MUSIC_SERVER_PORT = 8081;
    public static void main(String[] args){
                
        DatabaseManager dbm = new DatabaseManager();
        Thread udpHandler = new DatagramThread(FINGERPRINT_SERVER_PORT, WEB_SERVER_PORT, MUSIC_SERVER_PORT);
        udpHandler.start();
        
        SpeakerManager speakerManger = new SpeakerManager(dbm);
        MusicOrchestrationManager musicManager = new MusicOrchestrationManager(dbm);
        musicManager.start();

        // Music http server
        try {
            if(args.length >= 1) {
                new MusicHttpServer(MUSIC_SERVER_PORT, args[0], musicManager).listMusic().start();
            } else {
                //new MusicHttpServer(MUSIC_SERVER_PORT, "/home/francesco/Music", musicManager).listMusic().start();
                //new MusicHttpServer(MUSIC_SERVER_PORT, "C:\\Users\\giaco\\Music", musicManager).listMusic().start();
                new MusicHttpServer(MUSIC_SERVER_PORT, "/home/vallasc/Musica", musicManager).listMusic().start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // WebApp http server
        new MainHttpServer(WEB_SERVER_PORT, 
            new ServerWebSocket(
                new WebSocketHandler(dbm, musicManager, speakerManger)
                ),
            dbm ).start();      

        //(new FingerprintAnalyzer(dbm)).start();
        try(ServerSocket serverSocket = new ServerSocket(FINGERPRINT_SERVER_PORT)){
            //only one connection at a time is accepted through the socket, that's the client, speakers are handled through websockets
            while(true){
                Socket clientSocket = serverSocket.accept();
                (new SocketHandler(clientSocket, dbm)).start();
            }
		} catch(IOException e){
			e.printStackTrace();
			return;
		}
        
    }
   

}
