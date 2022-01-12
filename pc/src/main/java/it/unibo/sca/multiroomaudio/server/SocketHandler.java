package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

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

        String json;
        
        String clientId;
        try {
            dOut = new DataOutputStream(clientSocket.getOutputStream());
            dIn = new DataInputStream(clientSocket.getInputStream());
            json = dIn.readUTF();
            MsgHello hello = gson.fromJson(json, MsgHello.class);
            clientId = hello.getId();
            if(dbm.isConnectedSocket(clientId)){//already connected
                dOut.writeUTF(gson.toJson(new MsgHelloBack("type=rejected", clientId)));
                return;
            }

            dbm.addConnectedSocketClient(clientId, hello);
            dOut.writeUTF(gson.toJson(new MsgHelloBack("type=client", clientId)));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Client myDevice = (Client) dbm.getDevice(clientId); 
        boolean currentStart;
        String roomId;
        while(isRunning){
            try {
                int i = 0;
                //find a change in the start/stop state
                currentStart = myDevice.getStart();
                roomId = myDevice.getActiveRoom();
                if(currentStart){
                    try {
                        clientSocket.setSoTimeout(0);
                    } catch (SocketException e) {}
                    //send start to the client
                    dOut.writeUTF(gson.toJson(new MsgOfflineServer(currentStart)));
                    dOut.flush();
                    do{
                        //if room id is null and start it means that i'm in the online phase cause idk which room i'm in
                        //wait do i need another thread for the computations on the fingerprints?
                        if(myDevice.getPlay())
                            myDevice.setFingerprints(gson.fromJson(dIn.readUTF(), APInfo[].class));
                        //otherwise i'm in the offline phase and i have to save the fingerprints for this client for that room    
                        else{
                            dbm.putScans(clientId, roomId, gson.fromJson(dIn.readUTF(), APInfo[].class));
                        }
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
                }else{
                        clientSocket.setSoTimeout(1000);
                    try{
                        if(clientSocket.getInputStream().read() == -1){
                            isRunning = false;
                            dbm.removeConnectedSocketClient(clientId);
                        }
                    }catch(SocketTimeoutException e){}
                }
            } catch (SocketException e) {
                //e.printStackTrace();
                System.out.println("Connection closed");
                dbm.removeConnectedSocketClient(clientId);
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
            }
        }
        System.out.println("STOP");
        //dbm.printFingerprintDb(clientId);
    }

}
