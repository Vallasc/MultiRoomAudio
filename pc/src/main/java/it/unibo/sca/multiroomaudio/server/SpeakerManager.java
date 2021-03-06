package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.server.socket_handlers.WebSocketHandler;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgMute;
import it.unibo.sca.multiroomaudio.shared.messages.player.MsgSpeakerList;
import it.unibo.sca.multiroomaudio.shared.model.Device;
import it.unibo.sca.multiroomaudio.shared.model.Speaker;

/**
 * Utility methods for handling speakers
 */
public class SpeakerManager {
    private final static Logger LOGGER = Logger.getLogger(SpeakerManager.class.getSimpleName());
    private final DatabaseManager dbm;

    public SpeakerManager(DatabaseManager dbm){
        this.dbm = dbm;
    }

    /**
     * Send updated speaker list to all clients
     */
    public void updateSpeakerList(){
        LOGGER.info("Update speaker list of all clients");
        List<Speaker> speakers = dbm.getConnectedWebSpeakers().stream()
                                                    .map( pair -> (Speaker) pair.getRight() )
                                                    .collect(Collectors.toList());
        List<Pair<Session, Device>> clients = dbm.getConnectedWebClients();
        clients.forEach((client) -> {
            sendSpeakersList(client.getLeft(), speakers);
        });
    }

    /**
     * Send speaker list to a certain session
     * @param session Session
     * @param speakers list of speakers
     */
    public void sendSpeakersList(Session session, List<Speaker> speakers){
        MsgSpeakerList message = new MsgSpeakerList(speakers);
        try {
            WebSocketHandler.sendMessage(session, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send message to update current speaker audio state
     */
    public void updateAudioState(){
        dbm.getConnectedWebDevices().stream()
            .filter(pair -> pair.getRight() instanceof Speaker)
            .forEach(pair -> {
                Speaker speaker = (Speaker) pair.getRight();
                System.out.println("DEBUG: updateAudioState -> " + speaker.getName() + " muted ->" + speaker.isMuted());
                try{
                    WebSocketHandler.sendMessage(pair.getLeft(), new MsgMute(speaker.isMuted()));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });
    }
    
}
