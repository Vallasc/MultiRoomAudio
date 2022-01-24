package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import it.unibo.sca.multiroomaudio.shared.messages.*;
import it.unibo.sca.multiroomaudio.shared.model.Client;

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

        String clientId;
        try {
            dOut = new DataOutputStream(clientSocket.getOutputStream());
            dIn = new DataInputStream(clientSocket.getInputStream());
            String json = dIn.readUTF();
            MsgHello hello = gson.fromJson(json, MsgHello.class);
            clientId = hello.getId();
            if( clientId== null || (clientId!= null&& dbm.isConnectedSocket(clientId))){ //already connected
                dOut.writeUTF(gson.toJson(new MsgHelloBack("type=rejected", clientId)));
                dOut.close();
                return;
            }

            dbm.addConnectedSocketClient(clientId, hello);
            dOut.writeUTF(gson.toJson(new MsgHelloBack("type=client", clientId)));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Client myDevice = (Client) dbm.getDevice(clientId); 
        myDevice.setStart(true, null);
        while(isRunning){
            try {
                // Find a change in the start/stop state
                if(myDevice.getStart()){
                    //send start to the client
                    dOut.writeUTF(gson.toJson(new MsgStartScan(true)));
                    dOut.flush();

                    APInfo[] currentAPInfo = gson.fromJson(dIn.readUTF(), APInfo[].class);
                    if(currentAPInfo != null){
                        if(myDevice.getActiveRoom() == null) {
                            //System.out.println("Current scan len:" + currentAPInfo.length);
                            myDevice.setFingerprints(currentAPInfo);
                        } else {
                            dbm.putScans(myDevice, Arrays.asList(currentAPInfo));
                        }
                    }
                }
            } catch (SocketException e) {
                //e.printStackTrace();
                System.out.println("Connection closed");
                isRunning = false;
            } catch(EOFException e) {
                System.out.println("EOF");
                isRunning = false;
            } catch (IOException e) {
                System.err.println("Error while reading from the socket");
                isRunning = false;
            }

            if(!dbm.isConnectedSocket(clientId) || !isRunning){
                isRunning = false;
                //dbm.setDeviceStop(clientId, nScan);
                myDevice.setStart(false, null);
                myDevice.setActiveRoom(null);
                myDevice.setPlay(false);
                try {
                    dOut.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        dbm.removeConnectedSocketClient(clientId);
        // Close websocket session
        dbm.removeConnectedWebDevicesAndDisconnect(clientId);
        System.out.println("STOP SERVING: " + clientId);
        
    }

}
