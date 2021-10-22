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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;

import it.unibo.sca.multiroomaudio.shared.messages.*;

public class MulThread extends Thread{

    private final String multicastIp = "232.232.232.232";
    private final int multicastPort = 8265;
    private final int bufferSize = 1024 * 4;
    private MulticastSocket mySocket;

    
    public void run(){
        InetSocketAddress group = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new MsgHelloBack(InetAddress.getLocalHost().getHostName().toString()));
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
        
       
        
        while(true){
           /*NOTE qua sto in ascolto di messaggi in multicast in cui mi dicono "AOH ME VOGLIO CONNETTE" e poi quando ricevo sto messaggio mando il pacchetto
            quante volte lo mando? Boh, cazzi sua*/
            /*this should be done in another thread that only reads packet and then sends them to a q, the consumer for the q can be tha main thread and it registers 
            all the things a client sent in a structure, actually it could be the same thing that then sends the ip over the multicast, like every 3 clients processed or every 
            time the q becomes empty after having read something*/
            /*or there's another thread more that is in charge of sending the ip over the multicast every time a client sends a message over the multicast*/
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
            /*
            try {
                mySocket.send(new DatagramPacket(data, data.length, group));
            } catch (IOException e) {
                System.err.println("Error while sending the packet throught the group");
                e.printStackTrace();
            }*/
        }
    }
}
