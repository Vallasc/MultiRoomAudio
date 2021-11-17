package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

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
        try {
            dOut = new DataOutputStream(clientSocket.getOutputStream());
            dIn = new DataInputStream(clientSocket.getInputStream());
            json = dIn.readUTF();
            MsgHello hello = gson.fromJson(json, MsgHello.class);
            Boolean res = dbm.connectedDevices.putIfAbsent(hello.getId(), true);
            if(res != null){//already connected
                dOut.writeUTF(gson.toJson(new MsgHelloBack("REJECTED")));
                return;
            }
            if(dbm.devices.containsKey(hello.getId()))
                dOut.writeUTF(gson.toJson(new MsgHelloBack(5000, "type=client")));
            else
                dOut.writeUTF(gson.toJson(new MsgHelloBack(5000, "type=newclient")));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
      
        while(isRunning){
            try {
                json = dIn.readUTF();
                APInfo ap = gson.fromJson(json, APInfo.class);
                //System.out.println(ap + "\n");
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
