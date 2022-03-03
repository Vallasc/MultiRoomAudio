package it.unibo.sca.multiroomaudio.server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import it.unibo.sca.multiroomaudio.server.http_server.MainHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.MusicHttpServer;
import it.unibo.sca.multiroomaudio.server.http_server.ServerWebSocket;
import it.unibo.sca.multiroomaudio.server.socket_handlers.DatagramThread;
import it.unibo.sca.multiroomaudio.server.socket_handlers.SocketHandler;
import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
import it.unibo.sca.multiroomaudio.shared.model.Client;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;
public class ServerMain {

    private final static int FINGERPRINT_SERVER_PORT = 8497;
    private final static int WEB_SERVER_PORT = 8082;
    private final static int MUSIC_SERVER_PORT = 8081;
    static class ShutDownHandler extends Thread {
        private final DatabaseManager dbm;
        private final String filepath;
        public ShutDownHandler(DatabaseManager dbm, String filepath){
            this.dbm = dbm;
            this.filepath = filepath;
        }
        public void run() {
            try (Writer writer = new FileWriter(filepath + "\\devices.json")) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(dbm.getDevices(), writer);
            }catch(IOException e){
                System.err.println("error while serializing the devices file");
                e.printStackTrace();
            }

            try (Writer writer = new FileWriter(filepath + "\\fingerprints.json")) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(dbm.getClientRooms(), writer);
            }catch(IOException e){
                System.err.println("error while serializing the fingerprints file");
                e.printStackTrace();
            }
            System.out.println("Bye.");
        }
    }

    private static String handleOption(String opt, String[] args, int i, int len){
        if(i < len && !args[i].startsWith("-")){
            System.out.println(opt + " value is: " + args[i]);
            return args[i];
        }
        else{
            System.err.println(opt + " option needs an argument");
            return null;
        }
    }
    public static void main(String[] args){
        //default values
        String musicHttpLocation = "E:\\francesco\\music";
        //String musicHttpLocation = "C:\\Users\\giaco\\Music";
        //String musicHttpLocation = "/home/vallasc/Musica";
        String filepath = "./db";
        int i = 0;
        boolean flagResume = false;
        DatabaseManager dbm = new DatabaseManager();
        while(i<args.length){
            if(args[i].startsWith("-")){ 
                if(args[i].equals("-m") || args[i].equals("-music")){
                    i += 1;
                    musicHttpLocation = handleOption("-m", args, i, args.length);
                }
                else if(args[i].equals("-f") || args[i].equals("-filepath")){
                    i += 1;
                    filepath = handleOption("-f", args, i, args.length);
                }else if(args[i].equals("-r") || args[i].equals("-resume")){
                    flagResume = true;
                }
                else{
                    System.err.println("Error while parsing arguments, exiting");
                    return;
                }
                i++;
            }   
        }
        if(flagResume){
            Gson gson = new Gson();
            JsonReader reader;
            try {
                reader = new JsonReader(new FileReader(filepath+ "\\devices.json"));
                //Type type = new TypeToken<ArrayList<Device>>(){}.getType();
                JsonObject list = gson.fromJson(reader, JsonObject.class);
                int k = 0;
                while(k < list.size()){
                    JsonObject obj;
                    if(list.get(k+"").isJsonObject()){
                        obj = (JsonObject) list.get(k+"");
                        if(obj.get("type").getAsInt() == 0){
                            //client
                            System.out.println("resuming client: " +  obj.get("id").getAsString());
                            dbm.setDevices(new Client(obj.get("id").getAsString()));
                        }
                        else if(obj.get("type").getAsInt() == 1) {
                            //speaker
                            System.out.println("resuming speaker: " +  obj.get("id").getAsString());
                            dbm.setDevices(new Speaker(obj.get("id").getAsString(), obj.get("name").getAsString()));
                        }else{
                            //losing state unlucky
                            System.err.println("unrecognized type, losing everything");
                            return;
                        }            
                    }
                    k++;
                }

                reader = new JsonReader(new FileReader(filepath+ "\\fingerprints.json"));
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                dbm.putFingerprintsResume(json);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Runtime.getRuntime().addShutdownHook(new ShutDownHandler(dbm, filepath));
        Thread udpHandler = new DatagramThread(FINGERPRINT_SERVER_PORT, WEB_SERVER_PORT, MUSIC_SERVER_PORT);
        udpHandler.start();
        SpeakerManager speakerManger = new SpeakerManager(dbm);
        MusicOrchestrationManager musicManager = new MusicOrchestrationManager(dbm);
        musicManager.start();

        // Music http server
        try {
                new MusicHttpServer(MUSIC_SERVER_PORT, musicHttpLocation, musicManager).listMusic().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // WebApp http server
        new MainHttpServer(WEB_SERVER_PORT, 
            new ServerWebSocket(
                new WebSocketHandler(dbm, musicManager, speakerManger)
                ),
            dbm ).start();      

        try(ServerSocket serverSocket = new ServerSocket(FINGERPRINT_SERVER_PORT)){
            while(true){
                Socket clientSocket = serverSocket.accept();
                (new SocketHandler(clientSocket, dbm, speakerManger)).start();
            }
		} catch(IOException e){
			e.printStackTrace();
			return;
		}
    }
}