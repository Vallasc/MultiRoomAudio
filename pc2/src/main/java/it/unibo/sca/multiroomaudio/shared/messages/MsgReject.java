package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgReject extends Msg{

    String reason;

    public MsgReject(String reason) {
        super("REJECTED");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
    
}
