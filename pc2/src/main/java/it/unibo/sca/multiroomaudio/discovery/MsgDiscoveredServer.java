package it.unibo.sca.multiroomaudio.discovery;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgDiscoveredServer extends Msg{
    private int port;

    public MsgDiscoveredServer(int port) {
        super("DISCOVERED_SERVER");
        this.port = port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getPort(){
        return this.port;
    }
    
}
