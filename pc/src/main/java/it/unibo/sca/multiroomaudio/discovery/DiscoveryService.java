package it.unibo.sca.multiroomaudio.discovery;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.messages.Msg;

/**
 * Class that is used to discover the server in the local network
 */
public class DiscoveryService {
    private static final int bufferSize = 1024;
    public static final int DATAGRAM_PORT_SEND = 6262;
    public static final int DATAGRAM_PORT_RECEIVE = 6263;

    private List<InterfaceData> validInterfaces;
    private int interfaceIndex;

    private InetAddress serverAddress;
    private int webServerPort;
    private int musicServerPort;
    private int fingerprintPort;

    public DiscoveryService() {
        validInterfaces = new ArrayList<>();
        interfaceIndex = 0;
    }

    /**
     * Get all valid interfaces
     */
    private void getSpecs(){
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nInterface : interfaces){
                if(nInterface.isUp() && !nInterface.isLoopback() && !nInterface.isVirtual()) {
                    List<InterfaceAddress> addresses = nInterface.getInterfaceAddresses();
                    for (InterfaceAddress addr : addresses) {
                        InetAddress inetAddr = addr.getAddress();
                        InetAddress broadcast = addr.getBroadcast();
                        // If it has a broadcast Ip then is valid
                        if (!(inetAddr instanceof Inet6Address) && broadcast != null) {
                            String ip = addr.getAddress().getHostAddress();
                            this.validInterfaces.add(new InterfaceData(broadcast, ip));
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Discover serever based on interfaces
     * @return
     */
    public boolean discover(){
        getSpecs();

        boolean found = false;
        DatagramPacket packetReceive = new DatagramPacket(new byte[bufferSize], bufferSize);

        for(interfaceIndex = 0; interfaceIndex < validInterfaces.size(); interfaceIndex++) {
            InetAddress broadcast = getBroadcast();

            byte[] data = null;

            try { // Craft discovery message
                data = (new MsgDiscovery()).toByteArray();
            } catch (IOException e) {
                System.err.println("Error while crafting the message");
                continue;
            }

            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(DATAGRAM_PORT_RECEIVE);
                socket.setSoTimeout(2000);
            } catch (SocketException e) {
                System.err.println("Error while creating Datagram socket");
                continue;
            }

            try {
                socket.send(new DatagramPacket(data, data.length, broadcast, DATAGRAM_PORT_SEND));
                System.out.println("The discovery packet is sent successfully");
            } catch (IOException e) {
                socket.close();
                System.err.println("Error while sending discovery message");
                continue;
            }

            try {
                socket.receive(packetReceive);
                System.out.println("Discovery ack received");
            } catch (IOException e) {
                socket.close();
                System.err.println("Error receiving discovery ack");
                continue;
            }

            socket.close();
            found = true;
            break;
        }

        if( found ) {
            try {
                MsgDiscoveredServer discovered = (MsgDiscoveredServer) Msg.fromByteArray(packetReceive.getData());
                serverAddress = packetReceive.getAddress();
                fingerprintPort = discovered.getFingerprintPort();
                webServerPort = discovered.getServerPort();
                musicServerPort = discovered.getMusicPort();
            }catch (ClassNotFoundException | IOException e){
                found = false;
            }
        } else {
            System.err.println("No server found");
        }

        return found;
    }

    public InetAddress getBroadcast(){
        if(interfaceIndex < validInterfaces.size())
            return validInterfaces.get(interfaceIndex).getBroadcast();
        else
            return null;
    }

    public String getIp(){
        if(interfaceIndex < validInterfaces.size())
            return validInterfaces.get(interfaceIndex).getIp();
        else
            return null;
    }

    public InetAddress getServerAddress(){
        return serverAddress;
    }

    public int getFingerprintPort(){
        return fingerprintPort;
    }

    public int getWebServerPort(){
        return webServerPort;
    }

    public int getMusicServerPort(){
        return musicServerPort;
    }


    private class InterfaceData {
        private InetAddress broadcast;
        private String ip;

        private InterfaceData(InetAddress broadcast, String ip) {
            this.broadcast = broadcast;
            this.ip = ip;
        }

        public InetAddress getBroadcast() {
            return broadcast;
        }

        public String getIp() {
            return ip;
        }

        @Override
        public String toString() {
            return "InterfaceData{" +
                    "broadcast=" + broadcast +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }
}