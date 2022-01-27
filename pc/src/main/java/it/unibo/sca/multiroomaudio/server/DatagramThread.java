package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class DatagramThread extends Thread{
    private final int bufferSize = 1024 * 4;

    public void run(){
        System.out.println("Broadcast receiver started");
        Thread datagramAnalyser = new DatagramExecutor();
        datagramAnalyser.start();
        byte buf[] = new byte[bufferSize];
        DatagramPacket datagramPacket = new DatagramPacket(buf, bufferSize);  
        try(DatagramSocket datagramSocket = new DatagramSocket(6262)){
            while(true){
                datagramSocket.receive(datagramPacket);
                ((DatagramExecutor) datagramAnalyser).getRequestQ().put(
                    new ImmutablePair<byte[], InetAddress>(datagramPacket.getData(), datagramPacket.getAddress()));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("That's an error in thread");
            e.printStackTrace();
        } 
    }
}
