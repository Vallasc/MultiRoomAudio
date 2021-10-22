package it.unibo.sca.multiroomaudio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ClientMain {
    private static final String multicastIp = "232.232.232.232";
    private static final int multicastPort = 8265;
    private static final int bufferSize = 1024 * 4;
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
            mySocket.setLoopbackMode(false);
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
        byte[] buffer = new byte[bufferSize];
        try {
            System.out.println("Waiting for messages");
            mySocket.receive(new DatagramPacket(buffer, bufferSize, group));
            /*once it's received there should just be a q.add(buffer);, but i need to know if this thing works or not*/
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object readObject = ois.readObject();
            if (readObject instanceof MsgHello) {
                MsgHello hello = (MsgHello) readObject;
                System.out.println("Message is: " + hello.getType() + hello.getDeviceType() + hello.getMACid());
            } else {
                System.out.println("The received object is not of type String!");
            }
        } catch (IOException e) {
            System.err.println("Error while reading the packet from the group");
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            System.err.println("Error while reading the object from the buffer");
        }
    }
}