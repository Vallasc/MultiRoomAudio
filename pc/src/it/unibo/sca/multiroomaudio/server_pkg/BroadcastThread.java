package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import it.unibo.sca.multiroomaudio.shared.IPFinder;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHelloBack;

public class BroadcastThread extends Thread{
    private final int bufferSize = 1024 * 4;
    private static byte[] data;

    public BroadcastThread(){
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

    public static void sendBroadcast(){
        IPFinder.getBroadcast();
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            socket.send(new DatagramPacket(data, data.length, IPFinder.getBroadcast(), 6262));
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("error in sending");
        }
    }

    public void run(){
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(6262);
        } catch (SocketException e) {
            e.printStackTrace();
        }  
        byte buf[] = new byte[bufferSize];
        DatagramPacket datagramPacket = new DatagramPacket(buf, bufferSize);  
        System.out.println("mmm hello?");
        while(true){
            try {
                datagramSocket.receive(datagramPacket);
                ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
                if (readObject instanceof MsgHello) {
                    MsgHello hello = (MsgHello) readObject;
                    System.out.println("Message is: " + hello.getType() + hello.getDeviceType() + hello.getMACid());
                    //and then i send my packet
                } else continue;            
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                
                e.printStackTrace();
            }  
        }
    }
}
