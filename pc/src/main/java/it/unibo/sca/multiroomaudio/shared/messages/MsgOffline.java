package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgOffline extends Msg{
    String id;

    public MsgOffline(boolean start, String id) {
        super("OFFLINE");
        this.id = id;
    }


    public String getId(){
        return id;
    }
}
