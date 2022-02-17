package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg {
    private String id;
    private boolean rejected;

    public MsgHelloBack(String id, boolean rejected) {
        super("HELLO_BACK");
        this.id = id;
        this.rejected = rejected;
    }

    public String getClientId() {
        return this.id;
    }

    public boolean isRejected(){
        return this.rejected;
    }
}
