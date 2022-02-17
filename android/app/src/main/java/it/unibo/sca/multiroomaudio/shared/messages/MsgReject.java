package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgReject extends Msg{

    final String reason;
    final boolean isDuplicate;
    final String newId;


    public MsgReject(String reason, boolean isDuplicate, String newId){
        super("REJECTED");
        this.reason = reason;
        this.isDuplicate = isDuplicate;
        this.newId = newId;
    }

    public MsgReject(String reason) {
        super("REJECTED");
        this.reason = reason;
        this.isDuplicate = false;
        this.newId = null;
    }

    public String getReason() {
        return reason;
    }
    
    public boolean getDuplicate() {
        return isDuplicate;
    }

    public String getNewId(){
        return newId;
    }
}
