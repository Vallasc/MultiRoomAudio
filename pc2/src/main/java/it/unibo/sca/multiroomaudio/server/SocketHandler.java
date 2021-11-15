package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class SocketHandler extends Thread{
    private Socket clientSocket;
    private DatabaseManager dbm;
    
    Gson gson = new Gson();
    public SocketHandler(Socket clientSocket, DatabaseManager dbm){
        this.clientSocket = clientSocket;
        this.dbm = dbm;
    }

    public void readConnection(){
        DataInputStream dIn = null;
        try {
            dIn = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error while creating the inputstream");
            e.printStackTrace();
        }
        
        try{
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream()); 
            String json = dIn.readUTF();
            System.out.println("read the first hello");
            MsgHello hello = gson.fromJson(json, MsgHello.class);
            if(hello.getDeviceType() == 0){
                if(dbm.alreadyConnected(hello.getMac())){
                    dOut.writeUTF(gson.toJson(new MsgReject("you are somehow already connected"))); 
                }else{
                    dOut.writeUTF(gson.toJson(new MsgHelloBack()));
                    dbm.putConnected(hello.getMac(), new Device(hello.getDeviceType(), hello.getMac()));
                }
            }
        }catch(IOException e){
            System.err.println("Error in writing on the socket");
        }
    }

    public void readFingerprints(){
        boolean isRunning = true;
        DataInputStream dIn = null;
        try {
            dIn = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error while creating the inputstream");
            e.printStackTrace();
        }
        //client 
        while(isRunning){
            List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
            try {
                String json = dIn.readUTF();
                APInfo[] aps = gson.fromJson(json, APInfo[].class);
                for(APInfo ap : aps){
                    fingerprints.add(new Fingerprint(ap));
                    System.out.println(ap);
                }
                    //then this thing goes into the db to check the position with respect to the saved fingerprints                
            } catch (SocketException e) {
                e.printStackTrace();
                System.out.println("Connection closed");
                isRunning = false;
            }catch(EOFException e) {
                System.out.println("EOF");
            }
            catch (IOException e) {
                System.err.println("Error while reading from the socket");
                e.printStackTrace();
                isRunning = false;
            } 
        }
    }

}
