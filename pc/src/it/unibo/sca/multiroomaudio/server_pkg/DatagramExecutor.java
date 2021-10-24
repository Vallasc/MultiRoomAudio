package it.unibo.sca.multiroomaudio.server_pkg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.*;

import it.unibo.sca.multiroomaudio.shared.messages.MsgHello;
public class DatagramExecutor extends Thread{
	private BlockingQueue<byte[]> requestQ;
	
	public DatagramExecutor() {
		//should pass the data structure in which client parameters should be saved
		requestQ  = new LinkedBlockingQueue<>();
	}
	
	//takes a message, extracts the information and saves it inside the data structure
	public static void saveData(MsgHello msg){

	}
	
	public void run() {
		while(true) {
			MsgHello hello = null;
			try {
				byte[] buffer = requestQ.take();
				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Object readObject = ois.readObject();
                if (readObject instanceof MsgHello) {
                    hello = (MsgHello) readObject;
                    System.out.println("Message is: " + hello.getType() + hello.getDeviceType() + hello.getMACid());
                } else continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Problem using the buffer");
			} catch(ClassNotFoundException e){
				System.err.println("Cannot read the object");
			}
			//unpack and save data
			if(hello != null){
				saveData(hello);
			}
		}
	} 
	
	public BlockingQueue<byte[]> getRequestQ() {
		return requestQ;
	}
}
