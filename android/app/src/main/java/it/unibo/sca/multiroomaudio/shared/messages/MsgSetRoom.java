package it.unibo.sca.multiroomaudio.shared.messages;

import java.util.List;

import it.unibo.sca.multiroomaudio.shared.dto.Fingerprint;

public class MsgSetRoom extends Msg {
    private final String room;
    private final List<Fingerprint> fingerprints;
    
    public MsgSetRoom(String room, List<Fingerprint> fingerprints) {
        super(MsgTypes.SET_ROOM);
        this.room = room;
        this.fingerprints = fingerprints;
    }

    public List<Fingerprint> getFingerprints() {
        return fingerprints;
    }

    public String getRoom() {
        return room;
    }
    
}
