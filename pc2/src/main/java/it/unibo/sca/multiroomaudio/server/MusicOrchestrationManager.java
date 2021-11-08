package it.unibo.sca.multiroomaudio.server;

import java.util.concurrent.ConcurrentLinkedQueue;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.messages.MsgPlay;

public class MusicOrchestrationManager extends Thread {
    //private ConcurrentLinkedQueue<Msg> in;

    public MusicOrchestrationManager() {
        //in = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {

    }


    public void play(Song song, int fromMs) {
        //Msg msg = new MsgPlay(song, fromMs);
        
    }
    
}
