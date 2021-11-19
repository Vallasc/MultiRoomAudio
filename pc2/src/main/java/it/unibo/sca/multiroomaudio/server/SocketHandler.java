package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class SocketHandler extends Thread{
    private final Socket clientSocket;
    private final DatabaseManager dbm;
    public SocketHandler(Socket clientSocket, DatabaseManager dbm){
        this.clientSocket = clientSocket;
        this.dbm = dbm;
    }

    @Override
    public void run(){
        boolean isRunning = true;
        DataInputStream dIn = null;
        DataOutputStream dOut = null;
        Gson gson = new Gson();
        if(clientSocket == null)
            return;

        String json;
        
        String clientId;
        try {
            dOut = new DataOutputStream(clientSocket.getOutputStream());
            dIn = new DataInputStream(clientSocket.getInputStream());
            json = dIn.readUTF();
            MsgHello hello = gson.fromJson(json, MsgHello.class);
            clientId = hello.getIp();
            
            Boolean res = dbm.connectedDevices.putIfAbsent(clientId, true);
            if(res != null){//already connected
                dOut.writeUTF(gson.toJson(new MsgHelloBack("REJECTED")));
                return;
            }
            if(dbm.devices.containsKey(clientId)){
                dOut.writeUTF(gson.toJson(new MsgHelloBack("type=client", clientId)));
                System.out.println("Contains");
            }else{
                dOut.writeUTF(gson.toJson(new MsgHelloBack("type=newclient", clientId)));
                dbm.devices.put(clientId, new Device(hello.getDeviceType(), hello.getMac(), clientId));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Device myDevice = dbm.devices.get(clientId);
        while(isRunning){
            try {
                json = dIn.readUTF();
                myDevice.setFingerprints(gson.fromJson(json, APInfo[].class));
            } catch (SocketException e) {
                System.out.println("Connection closed");
                isRunning = false;
            } catch (IOException e) {
                System.err.println("Error while reading from the socket");
                e.printStackTrace();
                isRunning = false;
            }
            if(!dbm.connectedDevices.containsKey(clientId))
                isRunning = false;
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
