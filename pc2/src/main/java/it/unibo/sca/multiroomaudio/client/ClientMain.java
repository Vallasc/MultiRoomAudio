package it.unibo.sca.multiroomaudio.client;

import java.io.IOException;
import java.net.*;

import it.unibo.sca.multiroomaudio.shared.*;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ClientMain {
    private static final int bufferSize = 1024;
    private final static int servport = 8497;
    private static InetAddress serverAddress;
    private static Couple specs;

    public static void main(String[] args){     
        if(!discovery()){
            System.err.println("Something went wrong during the discovery phase");
            return;
        }
        if(!specs()){
            System.err.println("Something went wrong during the specs sending phase");
            return;
        }

    }
    
    //maybe is better to throw an exception in case of failure but i don't care
    private static boolean discovery(){
        byte[] data = null;
        byte[] byteBuffer1 = new byte[bufferSize];  
        DatagramPacket packetReceive = new DatagramPacket(byteBuffer1, bufferSize);
        try{
            data = msgHandler.dtgmOutMsg(new MsgHello());
        }catch(IOException e){
            System.err.println("Error while sending the message");
            return false;
        }
        //---------------------------------------------------------
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6263);
            socket.setSoTimeout(10000);
        } catch (SocketException e) {
            System.err.println("SocketException");
            e.printStackTrace();
            return false;
        }

        specs = IPFinder.getSpecs();
        InetAddress broadcastAddr = specs.getInetAddr();      
        
        boolean flagResend = true;
        while(flagResend){
            try {
                socket.send(new DatagramPacket(data, data.length, broadcastAddr, 6262));
            } catch (UnknownHostException e) {
                System.err.println("Cannot send the packet, host uknown");
                socket.close();
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                socket.close();
                e.printStackTrace();
                return false;
            }
            System.out.println("The packet is sent successfully");  
            try {
                socket.receive(packetReceive);
                flagResend = false;
                System.out.println("packets received");
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                System.err.println("Error while trying to read the answer from the server");
                e.printStackTrace();       
                socket.close();
                return false;   
            } 
        }
        //ok got the message
        try {
            Object readObject = msgHandler.dtgmInMsg(packetReceive.getData());
            if (readObject instanceof MsgHelloBack) 
                serverAddress = packetReceive.getAddress();    
                //MsgHelloBack helloBack = (MsgHelloBack) readObject;
                //System.out.println("Message is: " + helloBack.getType());
        }catch (IOException e){
            System.err.println("Error while reading from the socket");
            socket.close();
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Error in casting the class");
            socket.close();
            return false;
        }
        socket.close();
        return true;
    }

    private static boolean specs(){
        Socket socket = null;
        try {
            socket = new Socket(serverAddress, servport);
            System.out.println("Connected");
            //do stuff here
            msgHandler.tcpOutMsg(socket, new MsgSpecs(0, IPFinder.buildMac(specs.getBytes())));
            socket.close();
        } catch (IOException e) {
            System.err.println("Errore while setting up the tcp socket");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}