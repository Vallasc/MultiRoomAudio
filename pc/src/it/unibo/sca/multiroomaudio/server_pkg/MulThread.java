package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;

public class MulThread extends Thread{

    private final String multicastIp = "232.232.232.232";
    private final int multicastPort = 8265;
    private MulticastSocket mySocket;

    
    public void run(){
        String msg = null;
        try{
            msg = InetAddress.getLocalHost().getHostName().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] arr = null;
        try {
            arr = msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {}
        
        try {
            InetAddress mcastaddr = InetAddress.getByName(multicastIp);
            InetSocketAddress group = new InetSocketAddress(mcastaddr, multicastPort);
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getByName("localhost"));
            MulticastSocket m_socket = new MulticastSocket(multicastPort);
            mySocket = m_socket;
            mySocket.joinGroup(group, netIf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        DatagramPacket multicastPacket;
        try{
            multicastPacket = new DatagramPacket(arr, arr.length, InetAddress.getByName(multicastIp), multicastPort);
        } catch (IOException | SecurityException | IllegalBlockingModeException | IllegalArgumentException e) {
            //needed for debug
            e.printStackTrace();
        }
        
        /*
        while(){
            qua sto in ascolto di messaggi in multicast in cui mi dicono "AOH ME VOGLIO CONNETTE" e poi quando ricevo sto messaggio mando il pacchetto
            quante volte lo mando? Boh, cazzi sua
            mySocket.send(multicastPacket);
        }*/
    }
}
