package it.unibo.sca.multiroomaudio.shared.messages;

import java.io.Serializable;

import com.google.gson.Gson;

public abstract class Msg implements Serializable {
	protected String type;
	
	public Msg(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public String toJson(Gson serializer) {
        return serializer.toJson(this);
    }

	public int getDeviceType() {
        return -1;
    }
}

