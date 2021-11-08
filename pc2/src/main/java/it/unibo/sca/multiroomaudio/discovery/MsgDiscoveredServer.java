package it.unibo.sca.multiroomaudio.discovery;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgTypes;

public class MsgDiscoveredServer extends Msg{
    private int port;

    public MsgDiscoveredServer(int port) {
        super(MsgTypes.DISCOVEREDSERVER);
        this.port = port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getPort(){
        return this.port;
    }
    
}
