package it.unibo.sca.multiroomaudio.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
        
    }

    @Override
    public void run() {
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
                System.out.println("type: " + type);
                if(type.equals("CLOSED_WS")){
                    isRunning = false;
                    socket.close();
                }else{
                    MsgOfflineServer msgO = gson.fromJson(json, MsgOfflineServer.class);
                    //if stop read again
                    if(!msgO.getStop()){
                        //if start send
                        APInfo[] APs = scanner.scanNetworks();
                        dOut.writeUTF(gson.toJson(APs));
                        dOut.flush();
                        //then wait for ack, a dIn is enough tbh
                        MsgAck msgA = gson.fromJson(dIn.readUTF(), MsgAck.class);
                        if(msgA.getType().equals("ACK")){
                            System.out.println("ACK");
                        }
                    }
                }               
            } catch(SocketException e) {
                System.out.println("Server closed connection");
                isRunning = false;
            } catch (OperatingSystemNotDefinedException | IOException e) {
                e.printStackTrace();
                isRunning = false;
            }  
            if(isRunning)
                try {
                    Thread.sleep(SECONDS_BETWEEN_SCANS * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

    }

    public void stopScanner(){
        isRunning = false;
    }
    
}
