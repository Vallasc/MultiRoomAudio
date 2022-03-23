package it.unibo.sca.multiroomaudio.shared.messages.settings;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

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
