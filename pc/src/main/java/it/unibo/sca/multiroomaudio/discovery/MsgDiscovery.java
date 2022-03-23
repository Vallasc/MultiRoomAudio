package it.unibo.sca.multiroomaudio.discovery;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

/**
 * Message discovery sent by client
 */
public class MsgDiscovery extends Msg{
    public MsgDiscovery() {
        super("DISCOVERY");
    }
}
