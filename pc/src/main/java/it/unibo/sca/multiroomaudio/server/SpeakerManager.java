package it.unibo.sca.multiroomaudio.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.websocket.api.Session;

import it.unibo.sca.multiroomaudio.shared.dto.Device;
import it.unibo.sca.multiroomaudio.shared.dto.Speaker;
import it.unibo.sca.multiroomaudio.shared.messages.MsgSpeakerList;

public class SpeakerManager {
    private final static Logger LOGGER = Logger.getLogger(SpeakerManager.class.getSimpleName());

    private final DatabaseManager dbm;

    public SpeakerManager(DatabaseManager dbm){
        this.dbm = dbm;
    }

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

    public void muteSpeaker(Speaker speaker){
        speaker.setMuted(true);
    }

    public void unMuteSpeaker(Speaker speaker){
        speaker.setMuted(false);
    }

    public void sendSpeakersList(Session session, List<Speaker> speakers){
        MsgSpeakerList message = new MsgSpeakerList(speakers);
        try {
            WebSocketHandler.sendMessage(session, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
