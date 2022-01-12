package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgAck extends Msg{
    private int n;
    
    public MsgAck(int n) {
        super("ACK");
        this.n = n;
    }
    
    public int getN(){
        return n;
    }
}
