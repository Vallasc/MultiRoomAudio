package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg {
    private int port;
    private String path;

    public MsgHelloBack(String type){
        super(type);
    }
    public MsgHelloBack(int port, String path) {
        super("HELLO_BACK");
        this.port = port;
        this.path = path;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getPort(){
        return this.port;
    }

    public String getPath(){
        return this.path;
    }
}
