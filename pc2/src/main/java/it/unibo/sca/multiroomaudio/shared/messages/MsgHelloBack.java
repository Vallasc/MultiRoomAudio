package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg {
    String id;

    public MsgHelloBack() {
        super("HELLO_BACK");
    }

    public MsgHelloBack(String id) {
        super("HELLO_BACK");
        this.id = id;
    }
/*
    public void setPort(int port){
        this.port = port;
    }
*/
    public String getId(){
        return this.id;
    }
}
