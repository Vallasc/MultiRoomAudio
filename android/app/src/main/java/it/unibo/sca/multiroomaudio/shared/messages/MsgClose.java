package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgClose extends Msg{
    private String ip;

    public MsgClose(String ip){
        super("CLOSE");
        this.ip = ip;
    }

    public String getIp(){
        return this.ip;
    }
}
