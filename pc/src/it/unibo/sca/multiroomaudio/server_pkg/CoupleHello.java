package it.unibo.sca.multiroomaudio.server_pkg;

import java.net.InetAddress;

public class CoupleHello {
    private byte[] data;
    private InetAddress sender;

    public CoupleHello(byte[] data, InetAddress sender){
        this.data = data;
        this.sender = sender;
    }

    public byte[] getData(){
        return this.data;
    }

    public InetAddress getSender(){
        return this.sender;
    }
}
