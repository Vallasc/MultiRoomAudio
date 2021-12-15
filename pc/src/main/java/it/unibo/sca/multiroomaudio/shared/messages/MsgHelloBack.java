package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgHelloBack extends Msg {
    private String path;
    String id;

    public MsgHelloBack() {
        super("HELLO_BACK");
    }

    public MsgHelloBack(String type){
        super(type);
    }

    public MsgHelloBack(String path, String id) {
        super("HELLO_BACK");
        this.path = path;
        this.id = id;
    }

    public String getPath(){
        return this.path;
    }

    public String getCompletePath(){
        return this.path+"&clientId="+this.id;
    }
}
