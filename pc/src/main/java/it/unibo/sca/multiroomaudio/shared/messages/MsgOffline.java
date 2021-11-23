package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgOffline extends Msg{
    private boolean start;
    String id;

    public MsgOffline(boolean start, String id) {
        super("OFFLINE");
        this.start = start;
        this.id = id;
    }
    
    public boolean getStart(){
        return start;
    }

    public String getId(){
        return id;
    }
}
