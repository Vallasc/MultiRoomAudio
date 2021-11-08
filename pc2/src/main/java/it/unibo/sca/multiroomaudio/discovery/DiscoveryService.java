package it.unibo.sca.multiroomaudio.discovery;

import java.io.IOException;
import java.net.*;

import it.unibo.sca.multiroomaudio.shared.*;
import it.unibo.sca.multiroomaudio.shared.exceptions.UknowknBroadcastException;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class DiscoveryService {
    private static final int bufferSize = 1024;
    
    //private static Couple specs;
    public static Pair<Integer, InetAddress> discover() throws UknowknBroadcastException{
        InetAddress serverAddress = null;
        byte[] data = null;
        byte[] byteBuffer1 = new byte[bufferSize];  
        DatagramPacket packetReceive = new DatagramPacket(byteBuffer1, bufferSize);
        try{
            data = msgHandler.dtgmOutMsg(new MsgDiscovery());
        }catch(IOException e){
            System.err.println("Error while sending the message");
            throw new UknowknBroadcastException("");
        }
        //---------------------------------------------------------
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6263);
            socket.setSoTimeout(10000);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new UknowknBroadcastException("SocketException in creating the socket");
        }

        Pair<byte[], InetAddress> specs = IPFinder.getSpecs();
        InetAddress broadcastAddr = specs.getV();      
        
        boolean flagResend = true;
        while(flagResend){
            try {
                socket.send(new DatagramPacket(data, data.length, broadcastAddr, 6262));
            } catch (UnknownHostException e) {
                socket.close();
                e.printStackTrace();
                throw new UknowknBroadcastException("Cannot send the packet, host uknown");
            } catch (IOException e) {
                socket.close();
                e.printStackTrace();
                throw new UknowknBroadcastException("");
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
                throw new UknowknBroadcastException("Error while trying to read the answer from the server");  
            } 
        }
        //ok got the message
        Integer msg = null;
        try {
            Object readObject = msgHandler.dtgmInMsg(packetReceive.getData());
            if (readObject instanceof MsgDiscoveredServer) 
                serverAddress = packetReceive.getAddress();  
                MsgDiscoveredServer discovered = (MsgDiscoveredServer) readObject;
                msg = discovered.getPort();
        }catch (IOException e){
            socket.close();
            throw new UknowknBroadcastException("Error while reading from the socket");
        } catch (ClassNotFoundException e) {
            socket.close();
            throw new UknowknBroadcastException("Error in casting the class");
        }
        socket.close();
        return new Pair<Integer, InetAddress>(msg, serverAddress);
    }
}