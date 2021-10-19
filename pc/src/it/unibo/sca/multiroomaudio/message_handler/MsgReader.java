package it.unibo.sca.multiroomaudio.message_handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

/*
reads a message from a buffer
 */
public class MsgReader {
	
	private Queue<Msg> q;
	private ByteBuffer buf;
	int msgType = -1, len=-1, pos=0;
	Boolean hflag=false;
	byte[] data;
	
	public MsgReader() {
	    this.buf = ByteBuffer.allocate(4*1024*1024);
	    q = new LinkedList<>();
	  }
	  /**
	   * Legge i messaggi dal buffer man mano che sono disponibili
	   */
	  void receiveMsg() {
	    if(msgType < 0) {
	     if(buf.hasRemaining()) {
	        msgType = buf.getInt();
	        receiveMsg();
	      }
	    } else if(len < 0) {
	      if(buf.hasRemaining()) {
	        len = buf.getInt();
	        data = new byte[len];
	        receiveMsg();
	     }
	    }else if(len==0) {
	    	q.add(new Msg(msgType));
	        len = -1;
	        msgType = -1;
	        pos = 0;
	    } else if(pos < len) {
	      while( buf.hasRemaining() && pos < len) {
	        data[pos++] = buf.get();
	      }
		  if(pos == len) {
	        q.add(new Msg(msgType, data));
	        len = -1;
	        msgType = -1;
	        pos = 0;
	      }
	    }
	  }

	  /**
	   * Legge i messaggi disponibili sul canale
	   * @param channel Il canale da cui leggere i messaggi
	   * @throws IOException
	   */
	  public void read(SocketChannel channel)	throws IOException{
		  this.buf.clear();
		  channel.read(this.buf);
		  this.buf.flip();
		  while(this.buf.hasRemaining()) {
			  receiveMsg();
		  }  
	  }

	  /**
	   * Restituisce il primo messaggio disponibile in coda
	   * @return Il primo messaggio disponibile, o null
	   */
	  public Msg getQElement() {
		  return q.poll();
	  }
	  
	  
}