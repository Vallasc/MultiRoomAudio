package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgOfflineServer extends Msg{
    boolean start = false;

    public MsgOfflineServer(boolean start) {
        super("OFFLINE_SERVER");
        this.start = start;
    }

    public boolean getStart(){
        return start;
    }
}
