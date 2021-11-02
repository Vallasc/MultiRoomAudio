package it.unibo.sca.multiroomaudio.shared.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class msgHandler {
    public static byte[] dtgmOutMsg(Msg msg) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        return baos.toByteArray();
    }

    public static Object dtgmInMsg(Object object) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) object);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    public static void tcpOutMsg(Socket socket, Msg msg) throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        // create an object output stream from the output stream so we can send an object through it
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(msg);
    }

    public static Msg tcpInMsg(Socket socket) throws IOException, ClassNotFoundException{
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream so we can read data from it.
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return (Msg) objectInputStream.readObject();
    }
}
