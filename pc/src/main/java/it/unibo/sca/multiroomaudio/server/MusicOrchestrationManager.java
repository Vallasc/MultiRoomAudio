package it.unibo.sca.multiroomaudio.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPause;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgPlay;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgStop;
import it.unibo.sca.multiroomaudio.shared.model.Device;

public class MusicOrchestrationManager extends Thread {
    private final static Logger LOGGER = Logger.getLogger(MusicOrchestrationManager.class.getSimpleName());

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
            Msg currentPlayerState;
            synchronized(musicPlayer) {
                currentPlayerState = musicPlayer.prepareMessage();
            }
            // Stream current player state to all web devices
            for(Pair<Session, Device> device : connectedDevices){
                Session session = device.getLeft();
                try {
                    WebSocketHandler.sendMessage(session, currentPlayerState);
                } catch (Exception e) {
                    //RIP
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
        try{
            this.databaseManager.getSongs().get(songId);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        synchronized (musicPlayer) {
            musicPlayer.play(songId, (long) (fromSec* 1000));
        }
        LOGGER.info("PLAYING: " + musicPlayer.getCurrentSong().getTitle());
    }

    public void pauseCurrentSong() {
        synchronized (musicPlayer) {
            musicPlayer.pause();
        }
        LOGGER.info("PAUSE: " + musicPlayer.getCurrentSong().getTitle());
    }
    
    public void stopCurrentSong() {
        synchronized (musicPlayer) {
                musicPlayer.stop();
        }
        LOGGER.info("STOP: " + musicPlayer.getCurrentSong().getTitle());
    }

    public void nextSong() {
        synchronized (musicPlayer) {
            musicPlayer.next();
        }
        LOGGER.info("NEXT: " + musicPlayer.getCurrentSong().getTitle());
    }

    public void prevSong() {
        synchronized (musicPlayer) {
            musicPlayer.prev();
        }
        LOGGER.info("PREV: " + musicPlayer.getCurrentSong().getTitle());  
    }

    public class Player {
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
            if( songs.size() > 0 && 
                currentSongTime >= songs.get(songIndex).getDuration() ) {
                next();
                return 0;
            }
            return currentSongTime;
        }  

        public Msg prepareMessage(){
            if( songs.size() == 0)
                return new MsgStop();
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
