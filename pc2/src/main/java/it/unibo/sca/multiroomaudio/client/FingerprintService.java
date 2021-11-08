package it.unibo.sca.multiroomaudio.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import io.github.vallasc.WlanScanner;
import io.github.vallasc.WlanScanner.OperatingSystemNotDefinedException;

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
        if(socket == null)
            isRunning=false;
        else{
            try{
                dOut = new DataOutputStream(socket.getOutputStream());
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
                APInfo[] APs = scanner.scanNetworks();
                for(APInfo AP : APs){
                    System.out.println(AP);
                    String json = gson.toJson(AP);
                    dOut.writeUTF(json);
                }
            } catch (OperatingSystemNotDefinedException | IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
           
            
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
