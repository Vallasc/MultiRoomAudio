package message_handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/* writes messages on the channel */
public class MsgWriter {

	private Queue<Msg> q;
	private ByteBuffer buf;
	private Msg msg;
	
	public MsgWriter() {
	    this.buf = ByteBuffer.allocate(4*1024*1024);
	    this.buf.flip();
	    this.msg=null;
	    this.q = new LinkedList<>();
	}
	  boolean htflag = false, hdflag = false;
	  int pos = 0;

    /* puts messages in the buffer when there's space*/
    void writeMessage() {
        if(msg != null) {
            if(!htflag) {
            if(this.buf.hasRemaining()) {
                this.buf.putInt(msg.getType());
                htflag = true;
                writeMessage();
            }
            } else if(!hdflag) {
            if(this.buf.hasRemaining()) {
                if(msg.getData()==null) {
                    this.buf.putInt(0);
                    htflag = false;
                    hdflag = false;
                    pos = 0;
                    msg=null;
                    return;
                }
                else {
                    this.buf.putInt(msg.getData().length);
                    hdflag = true;
                }
                writeMessage();
            }
            } else{
                
                while(buf.hasRemaining() && pos < msg.getData().length) {
                    buf.put(msg.getData()[pos++]);
            }

            if(pos == msg.getData().length) {
                htflag = false;
                hdflag = false;
                pos = 0;
                msg=null;
            }
            }
        }
    }

    /*writes a message on a channel*/
    public void write(SocketChannel channel) throws IOException {
        this.buf.clear();
        while(this.buf.hasRemaining() && (msg = q.poll()) != null) {
            writeMessage();
        }
        this.buf.flip();
        channel.write(this.buf);
    }

    public void addMsg(Msg m) {
        if(m == null) throw new NullPointerException();
        
        q.add(m);
    }

	  
    public void addMsg(int type, String msg) {
        if(msg == null) throw new NullPointerException();

        q.add(new Msg(type, msg));
    }

    
    public void addMsg(int type, int msg) {
        q.add(new Msg(type, msg));
    }

    
    public void addMsg(int type, byte[] msg) {
        if(msg == null) throw new NullPointerException();
        q.add(new Msg(type, msg));
    }

	
}