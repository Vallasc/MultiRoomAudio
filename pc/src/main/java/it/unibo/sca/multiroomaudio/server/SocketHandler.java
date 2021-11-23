package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
            clientId = hello.getId();
            
            if(dbm.connectedDevices.containsKey(clientId)){//already connected
                dOut.writeUTF(gson.toJson(new MsgHelloBack("REJECTED")));
                return;
            }
            dbm.connectedDevices.put(clientId, true);
            if(dbm.devices.putIfAbsent(clientId, new Device(hello.getDeviceType(), hello.getMac(), clientId)) != null){
                dOut.writeUTF(gson.toJson(new MsgHelloBack("type=client", clientId)));
            }else{
                dOut.writeUTF(gson.toJson(new MsgHelloBack("type=newclient", clientId)));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Device myDevice = dbm.devices.get(clientId);
        boolean previousState = dbm.connectedDevices.get(clientId);
        //state = false if running, true if stopped
        while(isRunning){
            try {
                Boolean currentState = dbm.connectedDevices.get(clientId);
                if(currentState == null ) {
                    dOut.writeUTF(gson.toJson(new MsgClosedWs()));
                    //dbm.devices.remove(clientId);
                }else{
                    if(previousState != currentState || !currentState){
                        previousState = currentState; 
                        dOut.writeUTF(gson.toJson(new MsgOfflineServer(currentState)));
                        dOut.flush();
                        if(!currentState){
                            myDevice.setFingerprints(gson.fromJson(dIn.readUTF(), APInfo[].class));
                            System.out.println("fingerprint");
                            dOut.writeUTF(gson.toJson(new MsgAck()));
                            dOut.flush();
                        }
                    }
                }
            } catch (SocketException e) {
                //e.printStackTrace();
                System.out.println("Connection closed");
                dbm.connectedDevices.remove(clientId);
                isRunning = false;
            }catch(EOFException e) {
                System.out.println("EOF");
                isRunning = false;
            }
            catch (IOException e) {
                System.err.println("Error while reading from the socket");
                e.printStackTrace();
                isRunning = false;
            }
            if(!dbm.connectedDevices.containsKey(clientId)){
                isRunning = false;
                try {
                    dOut.writeUTF(gson.toJson(new MsgClosedWs()));
                }catch(IOException e) {
                    e.printStackTrace();
                }
                System.out.println("closing connection socket");
            }
        }
    }

}
