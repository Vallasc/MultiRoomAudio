package it.unibo.sca.multiroomaudio.server_pkg;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Executor extends Thread{
    private ExecutorService handleConn;
	private BlockingQueue<ConnThread> requestQ;
	
	public Executor() {
		requestQ  = new LinkedBlockingQueue<>();
		this.handleConn = Executors.newCachedThreadPool();
	}
	
	
	public void run() {
		while(true) {
			try {
				this.handleConn.execute(requestQ.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} 
	
	public BlockingQueue<ConnThread> getRequestQ() {
		return requestQ;
	}
}
