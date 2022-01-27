package it.unibo.sca.multiroomaudio.shared.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.gson.Gson;

public abstract class Msg implements Serializable {
	static final long serialVersionUID = 1L;

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

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		return baos.toByteArray();
	}

	public static Msg fromByteArray(byte[] array) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(array);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Msg) ois.readObject();
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}

