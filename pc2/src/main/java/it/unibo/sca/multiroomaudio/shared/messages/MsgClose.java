package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgClose extends Msg{

    String id;
    public MsgClose(String id) {
        super("CLOSE");
        this.id = id;
    }
    
    public String getId(){
        return this.id;
    }
}
