package it.unibo.sca.multiroomaudio.shared.messages.player;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgPlay extends Msg {
    private Song song; // Used only by speaker
    private int songId;
    private float fromTimeSec;

    public MsgPlay(Song song, int songId, float fromTimeSec) {
        super("PLAY");
        this.song = song;
        this.fromTimeSec = fromTimeSec;
        this.songId = songId;
    }

    public MsgPlay(int songId, float fromTimeSec) {
        super("PLAY");
        this.song = null;
        this.fromTimeSec = fromTimeSec;
        this.songId = songId;
    }

    public int getSongId() {
        return songId;
    }

    public Song getSong() {
        return song;
    }

    public float getFromTimeSec() {
        return fromTimeSec;
    }

}
