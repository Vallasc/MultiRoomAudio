package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg{
    private int port;

    public MsgHelloBack(int port) {
        super(MsgTypes.HELLO_BACK);
        this.port = port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getPort(){
        return this.port;
    }
}
