package it.unibo.sca.multiroomaudio.discovery;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

import it.unibo.sca.multiroomaudio.shared.messages.MyMsgHandler;

public class DiscoveryService {
    private static final int bufferSize = 1024;
    String mac;
    InetAddress broadcast;
    InetAddress serverAddress;
    String ip;
    int serverPort;
    int fingerprintPort;
    boolean failed = true;
    int id;

    public DiscoveryService() {
        getSpecs();
        discover();
    }

    public String getMac(){
        return mac;
    }

    public InetAddress getBroadcast(){
        return broadcast;
    }

    public boolean getFailed(){
        return failed;
    }

    public InetAddress getServerAddress(){
        return serverAddress;
    }

    public int getFingerprintPort(){
        return fingerprintPort;
    }

    public int getServerPort(){
        return serverPort;
    }

    public int getId(){
        return id;
    }

    public String getIp(){
        return ip;
    }

    public static String buildMac(byte[] mac){
        StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
    }

    private void getSpecs(){
        Enumeration<NetworkInterface> ni = null;
        boolean flagFound = false;
        try {
            ni = NetworkInterface.getNetworkInterfaces();
            while (ni.hasMoreElements() && !flagFound) {
                NetworkInterface n = ni.nextElement();
                if(n.isUp() && !n.isLoopback() && !n.isVirtual()){
                    List<InterfaceAddress> addresses = n.getInterfaceAddresses();
                    for(InterfaceAddress addr : addresses){

                        InetAddress inetAddr = addr.getAddress();
                        if (inetAddr instanceof Inet6Address) continue;
                        Socket socket = new Socket();
                        socket.setReuseAddress(true);
                        try {
                            socket.bind(new InetSocketAddress(inetAddr, 15000));
                            try{
                                socket.connect(new InetSocketAddress("google.com", 80), 1000);
                            }catch(BindException e){
                                socket.close();
                                this.mac = buildMac(n.getHardwareAddress());
                                this.broadcast = addr.getBroadcast();
                                this.ip=addr.getAddress().getHostAddress();
                                return;
                            }
                            socket.close();
                            this.mac = buildMac(n.getHardwareAddress());
                            this.broadcast = addr.getBroadcast();
                            this.ip=addr.getAddress().getHostAddress();

                          }catch(IOException e) {
                                System.err.println("socket is still bound");
                                continue;
                          }
                    }
                }
                   
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    //private static Couple specs;
    public void discover(){
        byte[] data = null;
        byte[] byteBuffer1 = new byte[bufferSize];  
        DatagramPacket packetReceive = new DatagramPacket(byteBuffer1, bufferSize);
        try{
            data = MyMsgHandler.dtgmOutMsg(new MsgDiscovery());
        }catch(IOException e){
            System.err.println("Error while sending the message");
            return;
        }
        //---------------------------------------------------------
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(6263);
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        boolean flagResend = true;
        while(flagResend){
            try {
                socket.send(new DatagramPacket(data, data.length, broadcast, 6262));
            } catch (IOException e) {
                socket.close();
                flagResend = false;
                e.printStackTrace();
                return;
            } 
            System.out.println("The packet is sent successfully");  
            try {
                socket.receive(packetReceive);
                flagResend = false;
                System.out.println("packets received");
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                e.printStackTrace();       
                socket.close(); 
                flagResend = false;
                return;
            } 
        }
        try {
            Object readObject = MyMsgHandler.dtgmInMsg(packetReceive.getData());
            if (readObject instanceof MsgDiscoveredServer) 
                serverAddress = packetReceive.getAddress();  
            MsgDiscoveredServer discovered = (MsgDiscoveredServer) readObject;
            fingerprintPort = discovered.getFingerprintPort();
            serverPort = discovered.getServerPort();
        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
            socket.close();
            return;
        }
        socket.close();
        failed = false;
    }
}