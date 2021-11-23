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
            
            if(dbm.connectedSocketDevices.containsKey(clientId)){//already connected
                dOut.writeUTF(gson.toJson(new MsgHelloBack("REJECTED")));
                return;
            }
            dbm.connectedSocketDevices.put(clientId, new Device(hello.getDeviceType(), hello.getMac(), clientId));
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
        //we have to work on that
        //should be false on init cause it's built that way
        //state = false if running, true if stopped
        boolean currentStart;
        while(isRunning){
            try {
                int i = 0;
                //find a change in the start/stop state
                currentStart = myDevice.getStart();
                if(currentStart){
                    //send start to the client
                    dOut.writeUTF(gson.toJson(new MsgOfflineServer(currentStart)));
                    dOut.flush();
                    do{
                        //read the fingerprints (set fingerprint is for the online phase, for the offline phase is like a savefingerprints, next thing to do)
                        myDevice.setFingerprints(gson.fromJson(dIn.readUTF(), APInfo[].class));
                        //send the ack
                        System.out.println("ACK: " + i);
                        dOut.writeUTF(gson.toJson(new MsgAck(i)));
                        i++;
                        dOut.flush();
                        currentStart = myDevice.getStart();
                        //here the client is sleeping
                        dOut.writeUTF(gson.toJson(new MsgOfflineServer(currentStart)));
                        dOut.flush();
                    }while(currentStart);
                    //stopped, send stop to the client
                }

            } catch (SocketException e) {
                //e.printStackTrace();
                System.out.println("Connection closed");
                dbm.removeConnectedSocketDevice(clientId);
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
            if(!dbm.isConnectedSocket(clientId)){
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
