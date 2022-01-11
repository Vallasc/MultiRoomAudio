package it.unibo.sca.multiroomaudio.server.http_server;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;

public class MainHttpServer extends HttpServer {

    final DatabaseManager dbm;

    public MainHttpServer(int port, ServerWebSocket webSocket, DatabaseManager dbm) {
        super(port);
        super.setWebSocket(webSocket);
        this.dbm = dbm;
    }
    
    @Override
    public void run() {
        super.run();
        setRoutes();
    }

    public void setRoutes(){
    }
}
