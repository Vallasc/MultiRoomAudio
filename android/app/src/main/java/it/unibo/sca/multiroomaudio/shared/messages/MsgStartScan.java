package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgStartScan extends Msg{
    boolean start = false;

    public MsgStartScan(boolean start) {
        super("START_SCAN");
        this.start = start;
    }

    public boolean getStart(){
        return start;
    }
}
