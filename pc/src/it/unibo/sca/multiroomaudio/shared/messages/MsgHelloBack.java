package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg{
    private String ip;

    public MsgHelloBack() {
        super(MsgTypes.HELLO_BACK);
        //this.ip = ip;
    }

    public void setIP(String ip){
        this.ip = ip;
    }

    public String getIp(){
        return this.ip;
    }
}
