package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

import it.unibo.sca.multiroomaudio.shared.messages.*;

public class MulThread extends Thread{

    private final String multicastIp = "224.0.0.2";
    private final int multicastPort = 8262;
    private final int bufferSize = 1024 * 4;
    private MulticastSocket mySocket;
    private InetSocketAddress group;
    private byte[] data;

    public MulThread(){
        //init multicast group
        group = null;
        try {
            InetAddress mcastaddr = InetAddress.getByName(multicastIp);
            group = new InetSocketAddress(mcastaddr, multicastPort);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getByName("localhost"));
            MulticastSocket m_socket = new MulticastSocket(multicastPort);
            mySocket = m_socket;
            mySocket.joinGroup(group, netIf);
            mySocket.setLoopbackMode(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //init message to send
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new MsgHelloBack(InetAddress.getLocalHost().getHostAddress()));
        }catch(IOException e){
            System.err.println("Something went wrong while setting up the message with the ip");
            e.printStackTrace();
            return;
        }
        data = baos.toByteArray(); 
    }
    
    public void run(){
        Thread datagramAnalyser = new DatagramExecutor();
        datagramAnalyser.start();
        byte[] buffer = null;
        while(true){
            buffer = new byte[bufferSize]; 
            //take the buffer 
            try {
                System.out.println("Waiting for messages");
                mySocket.receive(new DatagramPacket(buffer, bufferSize, group));
                ((DatagramExecutor) datagramAnalyser).getRequestQ().put(buffer);

                
            } catch (IOException e) {
                System.err.println("Error while reading the packet from the group");
                e.printStackTrace();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            
            try {
                System.out.println("Sending the ip");
                mySocket.send(new DatagramPacket(data, data.length, group));
            } catch (IOException e) {
                System.err.println("Error while sending the packet throught the group");
                e.printStackTrace();
            }
        }
    }
}
