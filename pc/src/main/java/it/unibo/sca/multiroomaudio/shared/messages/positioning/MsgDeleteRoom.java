package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgDeleteRoom extends Msg {
    private final String roomId;
    
    public MsgDeleteRoom(String roomId) {
        super("DELETE_ROOM");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
    
}
