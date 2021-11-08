package it.unibo.sca.multiroomaudio.server;

import java.util.concurrent.ConcurrentHashMap;

import it.unibo.sca.multiroomaudio.shared.dto.Device;

public class DatabaseManager {
    public static ConcurrentHashMap<String, Device> connectedDevices = new ConcurrentHashMap<String, Device>();
}
