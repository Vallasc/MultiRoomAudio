package it.unibo.sca.multiroomaudio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ClientMain {
    private static final String multicastIp = "232.232.232.232";
    private static final int multicastPort = 8265;
    
    public static void main(String[] args){
        MulticastSocket mySocket = null;
        InetSocketAddress group = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new MsgHello(0, "macID"));
        }catch(IOException e){
            System.err.println("Something went wrong while setting up the message with the ip");
            e.printStackTrace();
            return;
        }
        byte[] data = baos.toByteArray();
        try {
            InetAddress mcastaddr = InetAddress.getByName(multicastIp);
            group = new InetSocketAddress(mcastaddr, multicastPort);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getByName("localhost"));
            MulticastSocket m_socket = new MulticastSocket(multicastPort);
            mySocket = m_socket;
            mySocket.joinGroup(group, netIf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mySocket.leaveGroup(InetAddress.getByName(multicastIp));
        DatagramPacket multicastPacket;
        try {
            mySocket.send(new DatagramPacket(data, data.length, group));
        } catch (IOException e) {
            System.err.println("error in sending the message");
            e.printStackTrace();
        }
    }
}