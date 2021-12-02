package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgOffline extends Msg{
    String id;
    String room;

    public MsgOffline(boolean start, String id, String room) {
        super("OFFLINE");
        this.id = id;
        this.room = room;
    }

    public String getId(){
        return id;
    }

    public String getRoom(){
        return room;
    }

}
