package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgScanRoom extends Msg{
    private String roomId;
    private boolean startScan;
    private int nCorners;
    public MsgScanRoom(boolean startScan, String roomId, int nCorners) {
        super("SCAN_ROOM");
        this.roomId = roomId;
        this.startScan = startScan;
        this.nCorners = nCorners;
    }

    public String getRoomId(){
        return roomId;
    }

    public boolean getStartScan(){
        return startScan;
    }

    public int getNCorners(){
        return nCorners;
    }
}
