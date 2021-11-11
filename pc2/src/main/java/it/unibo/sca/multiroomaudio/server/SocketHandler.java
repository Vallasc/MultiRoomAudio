package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;

public class SocketHandler extends Thread{
    private Socket clientSocket;
    private DatabaseManager dbm;
    public SocketHandler(Socket clientSocket, DatabaseManager dbm){
        this.clientSocket = clientSocket;
        this.dbm = dbm;
    }


    @Override
    public void run(){
        boolean isRunning = true;
        DataInputStream dIn = null;
        Gson gson = new Gson();
        dbm.putConnected("asd");
        try {
            dIn = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error while creating the inputstream");
            e.printStackTrace();
            isRunning = false;
        }
        if(clientSocket == null)
            isRunning = false;
        while(isRunning){
            List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
            try {
                String json = dIn.readUTF();
                APInfo[] aps = gson.fromJson(json, APInfo[].class);
                for(APInfo ap : aps)
                    fingerprints.add(new Fingerprint(ap));
                    //then this thing goes into the db to check the position with respect to the saved fingerprints                
            } catch (SocketException e) {
                System.out.println("Connection closed");
                isRunning = false;
            } catch (IOException e) {
                System.err.println("Error while reading from the socket");
                e.printStackTrace();
                isRunning = false;
            }
        }
    }

}
