package it.unibo.sca.multiroomaudio.server;

public class FingerprintAnalyzer extends Thread{
    
    private final DatabaseManager dbm;

    public FingerprintAnalyzer(DatabaseManager dbm) {
        this.dbm = dbm;
    }

    @Override
    public void run(){
    }
    
}
