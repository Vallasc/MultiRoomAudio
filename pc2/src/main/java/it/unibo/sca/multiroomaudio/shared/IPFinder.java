package it.unibo.sca.multiroomaudio.shared;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;




public class IPFinder {
    //return the address of the network interface

    public static String buildMac(byte[] mac){
        StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
		}
		return sb.toString();
    }

    public static Pair<byte[], InetAddress> getSpecs(){
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
                        try {
                            socket.bind(new InetSocketAddress(inetAddr, 12351));
                            socket.connect(new InetSocketAddress("google.com", 80), 1000);
                            socket.close();
                            return new Pair<byte[], InetAddress>(n.getHardwareAddress(), addr.getBroadcast());
                          } catch (IOException ex) {
                            continue;
                          }
                    }
                }
                   
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;       
    }
}
