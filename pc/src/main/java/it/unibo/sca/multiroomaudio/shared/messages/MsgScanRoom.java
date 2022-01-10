package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgScanRoom extends Msg{
    String id;
    String room;

    public MsgScanRoom(boolean start, String id, String room) {
        super("SCAN_ROOM");
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
