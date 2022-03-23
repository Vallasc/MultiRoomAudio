package it.unibo.sca.multiroomaudio.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;
import io.github.vallasc.WlanScanner;
import io.github.vallasc.WlanScanner.OperatingSystemNotDefinedException;
import it.unibo.sca.multiroomaudio.shared.messages.positioning.MsgScanResult;
import it.unibo.sca.multiroomaudio.shared.messages.settings.MsgStartScan;
import it.unibo.sca.multiroomaudio.shared.model.ScanResult;
import it.unibo.sca.multiroomaudio.utils.Utils;

/**
 * Class that is used to scan the APs and send them to server 
 */
public class FingerprintService extends Thread {
    static final int MILLISECONDS_BETWEEN_SCANS = 300;

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
        
        DataOutputStream dOut = null;
        DataInputStream dIn = null;
        if(socket == null)
            return;

        try {
            dOut = new DataOutputStream(socket.getOutputStream());
            dIn = new DataInputStream(socket.getInputStream());
        } catch(IOException e) {
            System.err.println("Cannot create a dataoutput stream");
            e.printStackTrace();
            return;
        }

        //send this through the socket
        Gson gson = new Gson();
        while (true) {
            try {
                String json = dIn.readUTF();
                MsgStartScan msg = gson.fromJson(json, MsgStartScan.class);
                //if stop read again
                if(msg.getStart()) {
                    //if start send
                    try{
                        ScanResult[] result = apInfoToScanResult(scanner.scanNetworks());
                        MsgScanResult msgResult = new MsgScanResult(result);
                        dOut.writeUTF( gson.toJson(msgResult) );
                    } catch(com.google.gson.JsonSyntaxException e) {
                        System.out.println("Message parsing error");
                        continue;
                    }
                    dOut.flush();
                    Utils.sleep(MILLISECONDS_BETWEEN_SCANS);
                } else {
                    Utils.sleep(500);
                }          
            } catch (OperatingSystemNotDefinedException | IOException e) {
                System.out.println("Disconnected");
                e.printStackTrace();
                break;
            }                  
        }

        try {
            socket.close();
        } catch (IOException e) {}

    }

    public void stopScanner()  {
        isRunning = false;
    }

    private ScanResult[] apInfoToScanResult(APInfo[] aps) {
        if(aps == null)
            return new ScanResult[0];
        ScanResult[] result = new ScanResult[aps.length];
        for(int i = 0; i < aps.length; i++)
            result[i] = new ScanResult(aps[i].getBSSID(), 
                                        aps[i].getSSID(), 
                                        aps[i].getSignal(), 
                                        aps[i].getFrequency(), 
                                        System.currentTimeMillis());
        return result;
    }
    
}
