package it.unibo.sca.multiroomaudio.shared.messages;

import java.io.Serializable;

public abstract class Msg implements Serializable {
	private MsgTypes type;
	
	public Msg(MsgTypes type) {
		this.type = type;
	}

	public MsgTypes getType() {
		return this.type;
	}

}

