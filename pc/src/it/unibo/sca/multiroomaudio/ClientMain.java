package it.unibo.sca.multiroomaudio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import it.unibo.sca.multiroomaudio.shared.IPFinder;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class ClientMain {
    private static final String multicastIp = "224.0.0.2";
    private static final int multicastPort = 8262;
    private static final int bufferSize = 1024 * 4;
    private final static int servport = 8497;

    public static void main(String[] args){
        /*boolean flagReceivedIp = false;
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
            mySocket.setLoopbackMode(true);
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
        MsgHelloBack hello = null;
        try {
            while(hello == null){
                System.out.println("Waiting for messages");
                mySocket.receive(new DatagramPacket(buffer, bufferSize, group));
                /*once it's received there should just be a q.add(buffer);, but i need to know if this thing works or not
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
                if (readObject instanceof MsgHelloBack) {
                    hello = (MsgHelloBack) readObject;
                    System.out.println("Message is: " + hello.getType() + " " + hello.getIp());;
                } else continue;
            }
        } catch (IOException e) {
            System.err.println("Error while reading the packet from the group");
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            System.err.println("Error while reading the object from the buffer");
        }
        try {
            Socket socket = new Socket(InetAddress.getByName(hello.getIp()), servport);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("in teoria sono connesso");*/
        //IPFinder.getBroadcast();
        byte[] data;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new MsgHello(1, "Mac"));
        }catch(IOException e){
            System.err.println("Something went wrong while setting up the message with the ip");
            e.printStackTrace();
            return;
        }
        data = baos.toByteArray(); 
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("SocketException");
        }  
        InetAddress address = null;
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.err.println("Uknwonk host");
        }  
        //byte[] byteBuffer1 = new byte[2];  
        DatagramPacket packet;
        try {
            packet = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.1.255"), 6262);
            socket.send(packet);
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }  
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("The packets are sent successfully");  
        /*socket.receive(packetReceive);  
        System.out.println("The packet data received are: " + Arrays.toString(packetReceive.getData()));  */
    }
}