package it.unibo.sca.multiroomaudio.shared.messages;

import java.io.Serializable;

public abstract class Msg implements Serializable {
	protected String type;
	
	public Msg(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

}

