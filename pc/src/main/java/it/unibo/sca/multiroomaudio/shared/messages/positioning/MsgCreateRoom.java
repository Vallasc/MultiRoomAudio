package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgCreateRoom extends Msg {
    private final String roomId;
    
    public MsgCreateRoom(String roomId) {
        super("CREATE_ROOM");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
    
}
