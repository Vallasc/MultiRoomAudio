package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;

public class MulThread extends Thread{

    private final String multicastIp = "232.232.232.232";
    private final int multicastPort = 8265;
    private static MulticastSocket m_socket;
    
    public void run(){
        try {
            m_socket = new MulticastSocket(multicastPort);
            
            m_socket.joinGroup(InetAddress.getByName(multicastIp));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*questo devo farlo dopo aver ricevuto un messaggio in multicast in cui mi dicono "AOH ME VOGLIO CONNETTE"*/
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
        try{
            DatagramPacket multicastPacket = new DatagramPacket(arr, arr.length, InetAddress.getByName(multicastIp), multicastPort);
            m_socket.send(multicastPacket);
        } catch (IOException | SecurityException | IllegalBlockingModeException | IllegalArgumentException e) {
            //needed for debug
            e.printStackTrace();
        }
    }
}
