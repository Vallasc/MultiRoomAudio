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
import java.util.concurrent.*;

import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
import it.unibo.sca.multiroomaudio.shared.messages.MsgHelloBack;
public class DatagramExecutor extends Thread{
	private BlockingQueue<CoupleHello> requestQ;
	private static byte[] data;
	
	public DatagramExecutor() {
		//should pass the data structure in which client parameters should be saved
		requestQ  = new LinkedBlockingQueue<>();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(new MsgHelloBack());
        }catch(IOException e){
            System.err.println("Something went wrong while setting up the message with the ip");
            e.printStackTrace();
            return;
        }
        data = baos.toByteArray(); 
	}
	
	
	private void sendBroadcast(InetAddress receiver){
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
		System.out.println("mm hello");
		while(true) {
			MsgHello hello = null;
			
			try {
				CoupleHello couple = requestQ.take();
				byte[] buffer = couple.getData();
				InetAddress sender = couple.getSender();
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
				//System.out.println(packet.getData());
                if (readObject instanceof MsgHello) {
                    hello = (MsgHello) readObject;
                    System.out.println("Message is: " + hello.getType());
					sendBroadcast(sender);
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
	
	public BlockingQueue<CoupleHello> getRequestQ() {
		return this.requestQ;
	}

}
