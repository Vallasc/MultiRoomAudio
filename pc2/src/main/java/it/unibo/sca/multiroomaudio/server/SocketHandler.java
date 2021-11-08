package it.unibo.sca.multiroomaudio.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import com.google.gson.Gson;

import io.github.vallasc.APInfo;

public class SocketHandler extends Thread{
    private Socket clientSocket;

    public SocketHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    @Override
    public void run(){
        boolean isRunning = true;
        DataInputStream dIn = null;
        Gson gson = new Gson();
        try {
            dIn = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error while creating the inputstream");
            e.printStackTrace();
            isRunning = false;
        }
        if(clientSocket == null)
            isRunning = false;
        while(isRunning){
            try {
                String json = dIn.readUTF();
                APInfo ap = gson.fromJson(json, APInfo.class);
                System.out.println(ap);
            } catch (SocketException e) {
                System.out.println("Connection closed");
                isRunning = false;
            } catch (IOException e) {
                System.err.println("Error while reading from the socket");
                e.printStackTrace();
                isRunning = false;
            }
        }
    }

}
