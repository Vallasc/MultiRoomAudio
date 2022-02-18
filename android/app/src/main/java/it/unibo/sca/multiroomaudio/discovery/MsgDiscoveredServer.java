package it.unibo.sca.multiroomaudio.discovery;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgDiscoveredServer extends Msg{
    private int fingerprintPort;
    private int serverPort;
    private int musicPort;

    public MsgDiscoveredServer(int fingerprintPort, int serverPort, int musicPort) {
        super("DISCOVERED_SERVER");
        this.fingerprintPort = fingerprintPort;
        this.serverPort = serverPort;
        this.musicPort = musicPort;
    }    

    public int getServerPort(){
        return serverPort;
    }

    public int getFingerprintPort(){
        return this.fingerprintPort;
    }

    public int getMusicPort(){
        return this.musicPort;
    }
    
}
