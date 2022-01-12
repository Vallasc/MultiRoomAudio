package it.unibo.sca.multiroomaudio.server;

import it.unibo.sca.multiroomaudio.shared.dto.Client;

public class FingerprintAnalyzer extends Thread{
    
    private final Client client;
    

    public FingerprintAnalyzer(Client client) {
        this.client = client;
    }

    @Override
    public void run(){
    }
    
}
