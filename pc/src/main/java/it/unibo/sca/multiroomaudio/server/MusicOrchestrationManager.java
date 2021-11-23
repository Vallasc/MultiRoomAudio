package it.unibo.sca.multiroomaudio.server;


import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MusicOrchestrationManager extends Thread {
    private ConcurrentLinkedQueue<Msg> in;
    private ArrayList<Song> songs;

    private Song currentPlaying = null;
    private int currentTimeSec = 0;
    private DatabaseManager databaseManager;

    public MusicOrchestrationManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        songs = new ArrayList<Song>();
    }

    @Override
    public void run() {
        super.run();
        
    }

    public void setMusicList(ArrayList<Song> songs){
        this.songs = songs;
    }

    public void playSong(int songId, int fromMs) {
        System.out.println("PLAYING id: " + songId);
        
    }

    public void pauseCurrentSong() {
        
    }
    
    public void stopCurrentSong() {
        
    }

    public void nextSong() {
        
    }

    public void prevSong() {
        
    }
}
