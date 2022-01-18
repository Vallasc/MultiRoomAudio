package it.unibo.sca.multiroomaudio.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.vallasc.APInfo;
import io.github.vallasc.WlanScanner;
import io.github.vallasc.WlanScanner.OperatingSystemNotDefinedException;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class FingerprintService extends Thread {
    static final int SECONDS_BETWEEN_SCANS = 2;

    final WlanScanner scanner;
    boolean isRunning = false;
    Socket socket = null;

    public FingerprintService(){
        scanner = new WlanScanner();
    }

    public FingerprintService(Socket socket){
        scanner = new WlanScanner();
        this.socket = socket;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run() {
        System.out.println("Fingerprint service: RUNNING");
        //client info always through websocket tho
        isRunning = true;
        
        DataOutputStream dOut = null;
        DataInputStream dIn = null;
        if(socket == null)
            isRunning=false;
        else{
            try{
                dOut = new DataOutputStream(socket.getOutputStream());
                dIn = new DataInputStream(socket.getInputStream());
            }catch(IOException e){
                System.err.println("Cannot create a dataoutput stream");
                e.printStackTrace();
                isRunning = false;
            }
        }
        //send this through the socket
        Gson gson = new Gson();
        while (isRunning) {
            try {
                String json = dIn.readUTF();
                String type = gson.fromJson(json, JsonObject.class).get("type").getAsString();
                if(type.equals("CLOSED_WS")){
                    isRunning = false;
                    socket.close();
                }else{
                    MsgOfflineServer msgO = gson.fromJson(json, MsgOfflineServer.class);
                    //if stop read again
                    if(msgO.getStart()){
                        //if start send
                        try{
                            APInfo[] APs = scanner.scanNetworks();
                            dOut.writeUTF(gson.toJson(APs));
                        }catch(com.google.gson.JsonSyntaxException e){
                            System.out.println("Disconnected badly 2");
                            isRunning = false;
                            continue;
                        }catch(EOFException e) {
                            System.out.println("Disconnected badly");
                            isRunning = false;
                        }
                        dOut.flush();
                        //then wait for ack, a dIn is enough tbh
                        MsgAck ack = gson.fromJson(dIn.readUTF(), MsgAck.class);
                        System.out.println("received  ack: "  + ack.getN());
                        try {
                            Thread.sleep(SECONDS_BETWEEN_SCANS * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }               
            } catch(SocketException e) {
                System.out.println("Closed connection");
                isRunning = false;
           
            } catch (OperatingSystemNotDefinedException | IOException e) {
                e.printStackTrace();
                isRunning = false;
            }                  
        }

    }

    public void stopScanner(){
        isRunning = false;
    }
    
}
