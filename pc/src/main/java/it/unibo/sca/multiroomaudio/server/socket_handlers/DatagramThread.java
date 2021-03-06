package it.unibo.sca.multiroomaudio.server.socket_handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.lang3.tuple.ImmutablePair;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;

/**
 * Handle datagram packets and send them to DatagramExecutor
 */
public class DatagramThread extends Thread {
    private final int bufferSize = 1024 * 4;
    private final int fingerprintServerPort;
    private final int webServerPort;
    private final int musicServerPort;
    private DatagramExecutor datagramAnalyser;
    private boolean stopped = false;

    public DatagramThread(int fingerprintServerPort, int webServerPort, int musicServerPort){
        this.fingerprintServerPort = fingerprintServerPort;
        this.webServerPort = webServerPort;
        this.musicServerPort = musicServerPort;
    }

    /**
     * Main loop
     */
    public void run(){
        System.out.println("Broadcast receiver started");
        datagramAnalyser = new DatagramExecutor(fingerprintServerPort, webServerPort, musicServerPort);
        datagramAnalyser.start();
        byte buf[] = new byte[bufferSize];
        DatagramPacket datagramPacket = new DatagramPacket(buf, bufferSize);  
        try(DatagramSocket datagramSocket = new DatagramSocket(DiscoveryService.DATAGRAM_PORT_SEND)){
            while(!stopped){
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

    /**
     * Stop service
     */
    public void stopService(){
        datagramAnalyser.stopService();
		stopped = true;
		this.interrupt();
	}
}
