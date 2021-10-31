package it.unibo.sca.multiroomaudio.client;

import java.io.IOException;

import io.github.vallasc.APInfo;
import io.github.vallasc.WlanScanner;
import io.github.vallasc.WlanScanner.OperatingSystemNotDefinedException;

public class FingerprintService extends Thread {
    static final int SECONDS_BETWEEN_SCANS = 2;

    final WlanScanner scanner;
    boolean isRunning = false;


    public FingerprintService(){
        scanner = new WlanScanner();
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                APInfo[] APs = scanner.scanNetworks();
                for(APInfo AP : APs){
                    System.out.println(AP);
                }
                System.out.println();
            } catch (OperatingSystemNotDefinedException | IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
            try {
                Thread.sleep(SECONDS_BETWEEN_SCANS * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopScanner(){
        isRunning = false;
    }
    
}
