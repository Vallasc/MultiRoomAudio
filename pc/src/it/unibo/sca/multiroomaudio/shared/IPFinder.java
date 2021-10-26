package it.unibo.sca.multiroomaudio.shared;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.List;

public class IPFinder {
    //return the address of the network interface
    public static InetAddress getBroadcast(){
        Enumeration<NetworkInterface> ni = null;
        boolean flagFound = false;
        InetAddress broadcast = null;
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
                            socket.bind(new InetSocketAddress(inetAddr, 8080));
                            socket.connect(new InetSocketAddress("google.com", 80), 1000);
                            socket.close();
                            System.out.println(inetAddr.toString());
                          } catch (IOException ex) {
                            System.out.println(socket.isBound() + inetAddr.toString());
                            continue;
                          }
                    }
                }
                   
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return broadcast;       
    }
}
