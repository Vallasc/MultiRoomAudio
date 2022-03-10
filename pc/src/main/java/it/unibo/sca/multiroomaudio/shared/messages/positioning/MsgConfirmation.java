package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import java.util.List;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgConfirmation extends Msg{
    private final List<String> rooms;

    public MsgConfirmation(List<String> rooms) {
        super("CONFIRMATION");
        this.rooms = rooms;
    }

    public List<String> getRooms() {
        return rooms;
    }
}
