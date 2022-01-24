package it.unibo.sca.multiroomaudio.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import io.github.vallasc.WlanScanner;
import io.github.vallasc.WlanScanner.OperatingSystemNotDefinedException;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class FingerprintService extends Thread {
    static final int MILLISECONDS_BETWEEN_SCANS = 100;

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
                MsgStartScan msg = gson.fromJson(json, MsgStartScan.class);
                //if stop read again
                if(msg.getStart()){
                    //if start send
                    try{
                        APInfo[] APs = scanner.scanNetworks();
                        dOut.writeUTF(gson.toJson(APs));
                    }catch(com.google.gson.JsonSyntaxException | EOFException e){
                        System.out.println("Disconnected badly");
                        isRunning = false;
                        continue;
                    }
                    dOut.flush();
                    sleep(MILLISECONDS_BETWEEN_SCANS);
                } else {
                    sleep(500);
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

    public void sleep(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
