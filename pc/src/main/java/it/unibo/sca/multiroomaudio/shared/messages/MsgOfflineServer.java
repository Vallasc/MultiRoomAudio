package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgOfflineServer extends Msg{

    boolean stop = false;

    public MsgOfflineServer(boolean stop) {
        super("OFFLINE_SERVER");
        this.stop = stop;
    }

    public boolean getStop(){
        return stop;
    }
    
}
