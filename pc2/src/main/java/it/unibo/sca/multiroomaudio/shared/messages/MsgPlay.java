package it.unibo.sca.multiroomaudio.shared.messages;

import java.util.List;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;

public class MsgPlay extends Msg {
    private List<Song> songList;
    private int songId;
    private int fromTimeMs;

    
    public MsgPlay(List<Song> songList, int songId, int fromMs) {
        super("PLAY");
        this.songList = songList;
        this.fromTimeMs = fromMs;
        this.songId = songId;
    }


    public int getSongId() {
        return songId;
    }


    public void setSongId(int songId) {
        this.songId = songId;
    }


    public List<Song> getSongList() {
        return songList;
    }


    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }


    public int getFromTimeMs() {
        return fromTimeMs;
    }


    public void setFromTimeMs(int fromTimeMs) {
        this.fromTimeMs = fromTimeMs;
    }

}
