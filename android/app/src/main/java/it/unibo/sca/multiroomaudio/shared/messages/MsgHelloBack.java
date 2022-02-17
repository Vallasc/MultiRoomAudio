package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg {
    private String path;
    private String id;
    private boolean rejected;

    public MsgHelloBack(String path, String id, boolean rejected) {
        super("HELLO_BACK");
        this.path = path;
        this.id = id;
        this.rejected = rejected;
    }

    public String getPath(){
        return this.path;
    }

    public String getCompletePath(){
        return getPath() + "&clientId=" + this.id;
    }

    public boolean isRejected(){
        return this.rejected;
    }
}
