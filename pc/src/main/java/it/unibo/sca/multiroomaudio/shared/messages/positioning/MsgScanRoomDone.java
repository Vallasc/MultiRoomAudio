package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgScanRoomDone extends Msg{
    private boolean allRoomDone;
    private boolean singlePositionDone;

    public MsgScanRoomDone(boolean allRoomDone, boolean singlePositionDone) {
        super("SCAN_ROOM_DONE");
        this.allRoomDone = allRoomDone;
        this.singlePositionDone = singlePositionDone;
    }

    public boolean isSinglePositionDone() {
        return singlePositionDone;
    }

    public boolean isAllRoomDone() {
        return allRoomDone;
    }
}
