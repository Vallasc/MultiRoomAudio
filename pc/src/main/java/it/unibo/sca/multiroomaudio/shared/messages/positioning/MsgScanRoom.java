package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgScanRoom extends Msg{
    private String roomId;

    public MsgScanRoom(String roomId) {
        super("SCAN_ROOM");
        this.roomId = roomId;
    }

    public String getRoomId(){
        return roomId;
    }

}
