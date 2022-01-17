package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgScanRoom extends Msg{
    private String roomId;
    private boolean startScan;
    private int nScan;
    public MsgScanRoom(boolean startScan, String roomId, int nScan) {
        super("SCAN_ROOM");
        this.roomId = roomId;
        this.startScan = startScan;
        this.nScan = nScan;
    }

    public String getRoomId(){
        return roomId;
    }

    public boolean getStartScan(){
        return startScan;
    }

    public int getNScan(){
        return nScan;
    }
}
