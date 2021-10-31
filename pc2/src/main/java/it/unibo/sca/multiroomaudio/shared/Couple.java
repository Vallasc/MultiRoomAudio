package it.unibo.sca.multiroomaudio.shared;

import java.net.InetAddress;

public class Couple {
    private byte[] bytes;
    private InetAddress inetAddr;

    public Couple(byte[] bytes, InetAddress inetAddr){
        this.bytes = bytes;
        this.inetAddr = inetAddr;
    }

    public byte[] getBytes(){
        return this.bytes;
    }

    public InetAddress getInetAddr(){
        return this.inetAddr;
    }
}
