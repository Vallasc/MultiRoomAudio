package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class BroadcastThread extends Thread{
    private final int bufferSize = 1024 * 4;
    

    public BroadcastThread(){
        
    }

    public void run(){
        Thread datagramAnalyser = new DatagramExecutor();
        DatagramSocket datagramSocket = null;
        datagramAnalyser.start();
        try {
            datagramSocket = new DatagramSocket(6262);
        } catch (SocketException e) {
            e.printStackTrace();
        }  
        byte buf[] = new byte[bufferSize];
        DatagramPacket datagramPacket = new DatagramPacket(buf, bufferSize);  
        while(true){
            try {
                datagramSocket.receive(datagramPacket);
                ((DatagramExecutor) datagramAnalyser).getRequestQ().put(new CoupleHello(datagramPacket.getData(), datagramPacket.getAddress()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  
        }
    }
}
