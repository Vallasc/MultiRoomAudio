package it.unibo.sca.multiroomaudio.shared.dto;

import org.eclipse.jetty.websocket.api.Session;

public class Device {
    
    private final int type; // 0 client, 1 speaker, 2 listening client
    private final Session session;
    private final String name;
    private final String id;

    public Device(){
        type = -1;
        session = null;
        name = null;
        id = null;
    }

    public Device(int type, Session session, String name, String id) {
        this.type = type;
        this.session = session;
        this.name = name;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }


    public Session getSession() {
        return session;
    }


    public String getName() {
        return name;
    }


}
