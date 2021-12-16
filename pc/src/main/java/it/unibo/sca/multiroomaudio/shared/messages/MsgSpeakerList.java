package it.unibo.sca.multiroomaudio.shared.messages;

import java.util.List;
import java.util.stream.Collectors;

import it.unibo.sca.multiroomaudio.shared.dto.Speaker;

public class MsgSpeakerList extends Msg {
    private List<SpeakerDTO> speakerList;
    
    public MsgSpeakerList(List<Speaker> speakers) {
        super("SPEAKER_LIST");
        speakerList = speakers.stream()
                            .map( s -> SpeakerDTO.fromSpeaker(s) )
                            .collect(Collectors.toList());
    }

    public List<SpeakerDTO> getSpeakerList() {
        return speakerList;
    }

}

class SpeakerDTO {
    private String name;
    private String id;
    private boolean isMuted;
    
    private SpeakerDTO() {}

    public static SpeakerDTO fromSpeaker(Speaker speaker) {
        SpeakerDTO dto = new SpeakerDTO();
        dto.name = speaker.getName();
        dto.id = speaker.getId();
        dto.isMuted = speaker.isMuted();
        return dto;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}