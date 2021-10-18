package message_handler;

import java.nio.ByteBuffer;

/**
 * @author Francesco Biancucci 545063
 * @file Msg.java
 * @brief formato dei messaggi scambiati
 *
 */

public class Msg {
	/*definizione dei vari tipi di messaggio*/
	private int type;
	private byte[] data;
	
	public Msg(int t) {
		this.type=t;
		this.data=null;
	}
		
	public Msg(int t, byte[] b) {
		if(b == null) throw new NullPointerException();

		this.type=t;
		this.data=b;
	}
	
	public Msg(int t, String s) {
		if(s == null) throw new NullPointerException();

		
		this.type=t;
		this.data=s.getBytes();
	}
	
	public Msg(int t, int i) {
		this.type=t;
		this.data=ByteBuffer.allocate(4).putInt(i).array();
	}

	public byte[] getData() {
		return data;
	}

	public String getSData() {
		 return new String(data);
	}
	
	public int getIData() {
		return ByteBuffer.wrap(data).getInt();
	}
	
	public int getType() {
		return type;
	}
}

