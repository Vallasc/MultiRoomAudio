package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.*;

import it.unibo.sca.multiroomaudio.shared.Couple;
import it.unibo.sca.multiroomaudio.shared.messages.*;

public class DatagramExecutor extends Thread{
	private BlockingQueue<Couple> requestQ;
	private static byte[] data;
	
	public DatagramExecutor() {
		//should pass the data structure in which client parameters should be saved
		requestQ  = new LinkedBlockingQueue<>();
        try {
			data = msgHandler.dtgmOutMsg(new MsgHelloBack());
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
			MsgHello hello = null;
			
			try {
				Couple couple = requestQ.take();
				InetAddress sender = couple.getInetAddr();
                Object readObject = msgHandler.dtgmInMsg(couple.getBytes());
				//System.out.println(packet.getData());
                if (readObject instanceof MsgHello) {
                    hello = (MsgHello) readObject;
                    System.out.println("Message is: " + hello.getType());
					sendDatagram(sender);
                } else continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Problem using the buffer");
			} catch(ClassNotFoundException e){
				System.err.println("Cannot read the object");
			}
		}
	} 
	
	public BlockingQueue<Couple> getRequestQ() {
		return this.requestQ;
	}

}
