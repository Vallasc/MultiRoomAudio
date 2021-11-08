package it.unibo.sca.multiroomaudio.shared.messages;

import java.util.List;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;

public class MsgPlay extends Msg {
    private List<Song> songList;
    private int songId;
    private float fromTimeSec;

    
    public MsgPlay(List<Song> songList, int songId, float fromSec) {
        super("PLAY");
        this.songList = songList;
        this.fromTimeSec = fromSec;
        this.songId = songId;
    }


    public int getSongId() {
        return songId;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public float getFromTimeSec() {
        return fromTimeSec;
    }

}
