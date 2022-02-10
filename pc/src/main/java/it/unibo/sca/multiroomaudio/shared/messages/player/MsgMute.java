package it.unibo.sca.multiroomaudio.shared.messages.player;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

public class MsgMute extends Msg{
    boolean isMuted;
    
    public MsgMute(boolean isMuted) {
        super("MUTE");
        this.isMuted = isMuted;
    }
    
}
