package it.unibo.sca.multiroomaudio.discovery;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgTypes;

public class MsgDiscovery extends Msg{

    public MsgDiscovery() {
        super(MsgTypes.DISCOVERY);
    }
    
}
