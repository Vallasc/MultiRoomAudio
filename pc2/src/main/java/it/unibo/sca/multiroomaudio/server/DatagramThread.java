package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import it.unibo.sca.multiroomaudio.shared.Couple;

public class DatagramThread extends Thread{
    private final int bufferSize = 1024 * 4;

    public void run(){
        System.out.println("Broadcast receiver started");
        Thread datagramAnalyser = new DatagramExecutor();
        DatagramSocket datagramSocket = null;
        datagramAnalyser.start();
        try{
            datagramSocket = new DatagramSocket(6262); // TODO close datagram socket
        } catch (SocketException e) {
            e.printStackTrace();
        }  
        byte buf[] = new byte[bufferSize];
        DatagramPacket datagramPacket = new DatagramPacket(buf, bufferSize);  
        while(true){
            try {
                datagramSocket.receive(datagramPacket);
                ((DatagramExecutor) datagramAnalyser).getRequestQ().put(new Couple(datagramPacket.getData(), datagramPacket.getAddress()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  
        }
        //datagramSocket.close();
    }
}