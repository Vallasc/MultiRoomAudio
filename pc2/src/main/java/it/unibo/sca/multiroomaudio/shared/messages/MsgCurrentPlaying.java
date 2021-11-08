package it.unibo.sca.multiroomaudio.shared.messages;

public class MsgCurrentPlaying extends Msg {
    private int songId;
    private float currentTimeSec;

    public MsgCurrentPlaying(int songId, float currentTimeSec ) {
        super("CURRENT_PLAYING");
        this.songId = songId;
        this.currentTimeSec = currentTimeSec;
    }

    public int getSongId() {
        return songId;
    }


    public float getCurrentTimeSec() {
        return currentTimeSec;
    }

}
