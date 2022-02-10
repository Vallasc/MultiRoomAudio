package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgBindSpeaker extends Msg {

    private String speakerId;
    private String roomId;

    public MsgBindSpeaker(String speakerId, String roomId) {
        super("BIND_SPEAKER");
        this.speakerId = speakerId;
        this.roomId = roomId;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public String getRoomId() {
        return roomId;
    }
    
}
