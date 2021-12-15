package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPause;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPlay;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgStop;

public class MusicOrchestrationManager extends Thread {
    private final static Logger LOGGER = Logger.getLogger(MusicOrchestrationManager.class.getName());

    private int state = 0; // 0 = stop, 1 = play, 2 = pause
    private DatabaseManager databaseManager;
    private Player musicPlayer;

    public MusicOrchestrationManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        musicPlayer = new Player(databaseManager.getSongs());
    }

    @Override
    public void run() {
        super.run();
        
        while(true){
            
            List<Pair<Session, Device>> connectedDevices = databaseManager.getConnectedWebDevices();
            // Stream to all
            synchronized (this) {
                for(Pair<Session, Device> speaker : connectedDevices){
                    Session session = speaker.getLeft();
                    try {
                        WebSocketHandler.sendMessage(session, musicPlayer.prepareMessage());
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void setMusicList(ArrayList<Song> songs){
        this.databaseManager.getSongs().addAll(songs);
    }

    public void playSong(int songId, float fromSec) {
        Song song = this.databaseManager.getSongs().get(songId);
        synchronized (this) {
            musicPlayer.play(songId, (long) (fromSec* 1000));
            state = 1;
            LOGGER.info("PLAYING: " + musicPlayer.getCurrentSong().getTitle());
        }
    }

    public void pauseCurrentSong() {
        synchronized (this) {
            if(state == 1){
                musicPlayer.pause();
                state = 2;
                LOGGER.info("PAUSE: " + musicPlayer.getCurrentSong().getTitle());
            }
        }
    }
    
    public void stopCurrentSong() {
        synchronized (this) {
                musicPlayer.stop();
                state = 0;
            LOGGER.info("STOP: " + musicPlayer.getCurrentSong().getTitle());
        }
    }

    public void nextSong() {
        musicPlayer.next();
        LOGGER.info("NEXT: " + musicPlayer.getCurrentSong().getTitle());
    }

    public void prevSong() {
        musicPlayer.prev();
        LOGGER.info("PREV: " + musicPlayer.getCurrentSong().getTitle());  
    }

    public class Player { // TODO syncronize object?
        private List<Song> songs;
        private int songIndex = 0;
        private long startTime;
        private long pauseTime;
        private int state = 0; // 0 stop, 1 play, 2 pause

        Player(List<Song> songs){
            this.songs = songs;
            this.startTime = 0;
            this.pauseTime = 0;
        }

        public void play(int songIndex, long startMs) {
            if(this.songIndex != songIndex)
                stop();
            this.songIndex = songIndex;
            if(state == 0){ // Stopped
                state = 1;
                startTime = System.currentTimeMillis() - startMs;
            } else if(state == 1){ // Play
                stop();
                play(songIndex, startMs);
            } else if(state == 2) { // Paused
                state = 1;
                long pausedTime = System.currentTimeMillis() - pauseTime;
                startTime = startTime + pausedTime;
            }
        }

        public void pause() {
            if(state == 1){
                state = 2;
                pauseTime = System.currentTimeMillis();
            }
        }

        public void stop() {
            state = 0;
            startTime = 0;
            pauseTime = 0;
        }  

        public void next() {
            stop();
            if(songs.size() > songIndex + 1){
                play(++songIndex, 0);
            }
        }  

        public void prev() {
            if(getCurrentSongTimeMs() < 3000) {
                stop();
                if(songIndex > 0){
                    play(--songIndex, 0);
                } else {
                    play(songIndex, 0);
                }
            } else {
                stop();
                play(songIndex, 0);
            }
        }  

        public long getCurrentSongTimeMs() {
            long now = System.currentTimeMillis();
            long currentSongTime = 0;
            switch(state){
                case 1: // playing
                    currentSongTime = now - startTime;
                    break;
                case 2: // paused
                    currentSongTime = pauseTime - startTime;
                    break;
                case 0: // stopped
                    currentSongTime = 0;
                    break;
            }
            if( currentSongTime >= songs.get(songIndex).getDuration() ) { // New song in the list
                next();
                return 0;
            }
            return currentSongTime;
        }  

        public Msg prepareMessage(){
            float currentTimeSec = getCurrentSongTimeMs()/1000;
            Song song = songs.get(songIndex);
            switch(state){
                case 1:
                    return new MsgPlay(song, song.getId(), currentTimeSec);
                case 2:
                    return new MsgPause(song, song.getId(), currentTimeSec);
                case 0:
                default:
                    return new MsgStop();
            }
        }

        public Song getCurrentSong() {
            if(songs.size() > 0)
                return songs.get(songIndex);
            return null;
        }
    }
}
