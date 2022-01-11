package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgScanRoom extends Msg{
    private String roomId;
    private boolean startScan;

    public MsgScanRoom(boolean startScan, String roomId) {
        super("SCAN_ROOM");
        this.roomId = roomId;
        this.startScan = startScan;
    }

    public String getRoomId(){
        return roomId;
    }

    public boolean getStartScan(){
        return startScan;
    }
}
