package it.unibo.sca.multiroomaudio.shared.messages.positioning;

import java.util.List;
import java.util.stream.Collectors;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;
import it.unibo.sca.multiroomaudio.shared.model.Room;

public class MsgRooms extends Msg {
    private List<RoomDTO> rooms;

    public MsgRooms(List<Room> rooms) {
        super("ROOMS");
        this.rooms = rooms.stream()
                            .map((r)->new RoomDTO(r))
                            .collect(Collectors.toList());
    }

    public List<RoomDTO> getRooms(){
        return this.rooms;
    }

}

class RoomDTO {
    String roomId;
    int samples;

    RoomDTO(Room room){
        this.roomId = room.getId();
        this.samples = room.getFingerprintsSize();
    }
}