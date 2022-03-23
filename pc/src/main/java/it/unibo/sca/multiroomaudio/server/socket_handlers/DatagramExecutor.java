package it.unibo.sca.multiroomaudio.server.socket_handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.*;

import org.apache.commons.lang3.tuple.Pair;

import it.unibo.sca.multiroomaudio.discovery.DiscoveryService;
import it.unibo.sca.multiroomaudio.discovery.MsgDiscoveredServer;
import it.unibo.sca.multiroomaudio.discovery.MsgDiscovery;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;

/**
 * Class that haandles discovery messages
 */
public class DatagramExecutor extends Thread {
	private BlockingQueue<Pair<byte[], InetAddress>> requestQ;
	private static byte[] data;
	private boolean stopped = false;
	
	public DatagramExecutor(int fingerprintPort, int webServerPort, int musicServerPort) {
		//should pass the data structure in which client parameters should be saved
		requestQ  = new LinkedBlockingQueue<>();
        try {
			data = (new MsgDiscoveredServer(fingerprintPort, webServerPort, musicServerPort)).toByteArray();
		} catch (IOException e) {
			System.err.println("error while creating the message");
			e.printStackTrace();
		} 
	}
	
	/**
	 * Send datagram packet
	 * @param receiver destination address
	 */
	private void sendDatagram(InetAddress receiver){
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.send(new DatagramPacket(data, data.length, receiver, DiscoveryService.DATAGRAM_PORT_RECEIVE));
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("error in sending");
        }
    }

	/**
	 * Main loop
	 */
	public void run() {
		System.out.println("Datagram handler started");
		while(!stopped) {
			try {
				Pair<byte[], InetAddress> pair = requestQ.take();
				InetAddress sender = (InetAddress) pair.getRight();
                Object readObject = Msg.fromByteArray(pair.getLeft());
				//System.out.println(packet.getData());
                if (readObject instanceof MsgDiscovery) {
					System.out.println("Read a discovery msg");
					sendDatagram(sender);
                } else continue;
			} catch (InterruptedException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Problem using the buffer");
				e.printStackTrace();
			} catch(ClassNotFoundException e){
				System.err.println("Cannot read the object");
			}
		}
	} 
	
	public BlockingQueue<Pair<byte[], InetAddress>> getRequestQ() {
		return this.requestQ;
	}

	public void stopService(){
		stopped = true;
		this.interrupt();
	}

}
