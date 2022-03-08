package it.unibo.sca.multiroomaudio.server.socket_handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.server.FingerprintAnalyzer;
import it.unibo.sca.multiroomaudio.server.SpeakerManager;
import it.unibo.sca.multiroomaudio.server.localization_algorithms.Knn;
import it.unibo.sca.multiroomaudio.shared.messages.*;
import it.unibo.sca.multiroomaudio.shared.model.Client;

public class SocketHandler extends Thread{
    private final Socket clientSocket;
    private final DatabaseManager dbm;
    private final SpeakerManager speakerManager;
    private boolean isRunning;
    //private final Class algo;

    public SocketHandler(Socket clientSocket, DatabaseManager dbm, SpeakerManager speakerManager){
        this.clientSocket = clientSocket;
        this.dbm = dbm;
        this.speakerManager = speakerManager;
        //this.algo = algo;
    }

    @Override
    public void run(){
        isRunning = true;
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
            if( clientId== null || (clientId!= null && dbm.isConnectedSocket(clientId))){ //already connected
                System.out.println("Client [" + clientId + "] is already connected");
                dOut.writeUTF(gson.toJson(new MsgHelloBack(clientId, true)));
                dOut.close();
                return;
            }

            dbm.addConnectedSocketClient(clientId, hello);
            dOut.writeUTF(gson.toJson(new MsgHelloBack(clientId, false)));
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Client myDevice = (Client) dbm.getDevice(clientId); 
        myDevice.setActiveRoom(null);
        System.out.println("START SERVING: " + clientId);
        //FingerprintAnalyzer fAnalyzernew = new Bayes(speakerManager, myDevice, dbm);

        //Constructor<?> fAnalyzernew = this.algo.getConstructor(SpeakerManager.class, Client.class, DatabaseManager.class);
        //Constructor constructor = Class.forName("java.lang.String").getConstructor(String.class);
        //String object = (String) constructor.newInstance("Hello");

        FingerprintAnalyzer fAnalyzernew = new Knn(speakerManager, myDevice, dbm, 5, true, false);
        fAnalyzernew.start();
        while(isRunning){
            try {
                // Find a change in the start/stop state
                //send start to the client
                dOut.writeUTF(gson.toJson(new MsgStartScan(true)));
                dOut.flush();
                MsgScanResult resultMessage = gson.fromJson(dIn.readUTF(), MsgScanResult.class);
                //System.out.println(resultMessage.toJson());
                if(myDevice.getActiveRoom() == null) {
                    //System.out.println("Current scan len:" + resultMessage.getApList().length);
                    myDevice.setFingerprints(resultMessage.getApList());
                } else {
                    //System.out.println("Current scan len:" + resultMessage.getApList().length);
                    dbm.saveRoomScans(myDevice, resultMessage.getApList());
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
                try {
                    isRunning = false;
                    //dbm.setDeviceStop(clientId, nScan);
                    myDevice.setActiveRoom(null);
                    dOut.close();
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        fAnalyzernew.stopService();
        dbm.removeConnectedSocketClient(clientId);
        // Close websocket session
        dbm.removeConnectedWebDevicesAndDisconnect(clientId);
        System.out.println("STOP SERVING: " + clientId);
    }

    public void stopService(){
		isRunning = false;
		this.interrupt();
	}
}
