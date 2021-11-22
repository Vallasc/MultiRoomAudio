package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.*;

import it.unibo.sca.multiroomaudio.discovery.MsgDiscoveredServer;
import it.unibo.sca.multiroomaudio.discovery.MsgDiscovery;
import it.unibo.sca.multiroomaudio.shared.Pair;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class DatagramExecutor extends Thread {
	private BlockingQueue<Pair<byte[], InetAddress>> requestQ;
	private static byte[] data;
	
	public DatagramExecutor() {
		//should pass the data structure in which client parameters should be saved
		requestQ  = new LinkedBlockingQueue<>();
        try {
			data = MyMsgHandler.dtgmOutMsg(new MsgDiscoveredServer(8497, 80));
		} catch (IOException e) {
			System.err.println("error while creating the message");
			e.printStackTrace();
		} 
	}
	
	
	private void sendDatagram(InetAddress receiver){
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            socket.send(new DatagramPacket(data, data.length, receiver, 6263));
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("error in sending");
        }
    }

	public void run() {
		System.out.println("Datagram handler started");
		while(true) {
			try {
				Pair<byte[], InetAddress> pair = requestQ.take();
				InetAddress sender = (InetAddress) pair.getV();
                Object readObject = MyMsgHandler.dtgmInMsg((Object) pair.getU());
				//System.out.println(packet.getData());
                if (readObject instanceof MsgDiscovery) {
					System.out.println("Read a discovery msg");
					sendDatagram(sender);
                } else continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
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

}