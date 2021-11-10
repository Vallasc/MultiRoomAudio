package it.unibo.sca.multiroomaudio.discovery;

import java.io.IOException;
import java.net.*;

import it.unibo.sca.multiroomaudio.shared.*;
import it.unibo.sca.multiroomaudio.shared.messages.MyMsgHandler;

public class DiscoveryService {
    private static final int bufferSize = 1024;
    
    //private static Couple specs;
    public static Pair<Integer, InetAddress> discover(){
        InetAddress serverAddress = null;
        byte[] data = null;
        byte[] byteBuffer1 = new byte[bufferSize];  
        DatagramPacket packetReceive = new DatagramPacket(byteBuffer1, bufferSize);
        try{
            data = MyMsgHandler.dtgmOutMsg(new MsgDiscovery());
        }catch(IOException e){
            System.err.println("Error while sending the message");
            return new Pair<Integer, InetAddress>(-1, serverAddress);
        }
        //---------------------------------------------------------
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6263);
            socket.setSoTimeout(10000);
        } catch (SocketException e) {
            e.printStackTrace();
            return new Pair<Integer, InetAddress>(-1, serverAddress);
        }

        Pair<byte[], InetAddress> specs = IPFinder.getSpecs();
        InetAddress broadcastAddr = specs.getV();      
        
        boolean flagResend = true;
        while(flagResend){
            try {
                socket.send(new DatagramPacket(data, data.length, broadcastAddr, 6262));
            } catch (IOException e) {
                socket.close();
                e.printStackTrace();
                return new Pair<Integer, InetAddress>(-1, serverAddress);
            } 
            System.out.println("The packet is sent successfully");  
            try {
                socket.receive(packetReceive);
                flagResend = false;
                System.out.println("packets received");
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                e.printStackTrace();       
                socket.close(); 
                return new Pair<Integer, InetAddress>(-1, serverAddress);
            } 
        }
        //ok got the message
        Integer msg = null;
        try {
            Object readObject = MyMsgHandler.dtgmInMsg(packetReceive.getData());
            if (readObject instanceof MsgDiscoveredServer) 
                serverAddress = packetReceive.getAddress();  
                MsgDiscoveredServer discovered = (MsgDiscoveredServer) readObject;
                msg = discovered.getPort();
        }catch (ClassNotFoundException | IOException e){
            socket.close();
            return new Pair<Integer, InetAddress>(-1, null);
        }
        socket.close();
        return new Pair<Integer, InetAddress>(msg, serverAddress);
    }
}