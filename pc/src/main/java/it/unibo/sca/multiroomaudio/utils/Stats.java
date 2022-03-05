package it.unibo.sca.multiroomaudio.utils;

public class Stats {
    // Misurazioni
    // - Tempo di fase offline
    // - Tempo riconoscimento cambio stanza

    // - # posizion indovinate / posizioni totali

    private final static String filename = "stats.csv";

    private static Stats INSTANCE;
    private Stats() {}
    
    public static Stats getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Stats();
        }
        return INSTANCE;
    }

    
}
