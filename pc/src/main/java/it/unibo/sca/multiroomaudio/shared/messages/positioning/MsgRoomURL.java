package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgRoomURL extends Msg {
    private final String roomId;
    private final String urlEnter;
    private final String urlLeave;
    
    public MsgRoomURL(String roomId, String urlEnter, String urlLeave) {
        super("ROOM_URL");
        this.roomId = roomId;
        this.urlEnter = urlEnter;
        this.urlLeave = urlLeave;
    }

    public String getUrlLeave() {
        return urlLeave;
    }

    public String getUrlEnter() {
        return urlEnter;
    }

    public String getRoomId() {
        return roomId;
    }
    
}
