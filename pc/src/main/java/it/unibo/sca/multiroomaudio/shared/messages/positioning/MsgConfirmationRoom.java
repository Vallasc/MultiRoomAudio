package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgConfirmationRoom extends Msg{
    private final String roomId;
    
    public MsgConfirmationRoom(String roomId) {
        super("CONFIRMATION_ROOM");
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
