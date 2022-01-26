package it.unibo.sca.multiroomaudio.discovery;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgDiscoveredServer extends Msg{
    private int fingerprintPort;
    private int serverPort;

    public MsgDiscoveredServer(int fingerprintPort, int serverPort) {
        super("DISCOVERED_SERVER");
        this.fingerprintPort = fingerprintPort;
        this.serverPort = serverPort;
    }    

    public int getServerPort(){
        return serverPort;
    }

    public int getFingerprintPort(){
        return this.fingerprintPort;
    }
    
}
