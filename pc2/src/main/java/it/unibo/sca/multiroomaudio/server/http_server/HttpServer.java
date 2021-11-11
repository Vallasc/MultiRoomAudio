package it.unibo.sca.multiroomaudio.server.http_server;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import spark.Service;

public class HttpServer extends Thread {
    protected final String dirUri;
    protected final Service service;
    final DatabaseManager databaseManager;
    private final int port;
    private final int poolSize = 8;

    public HttpServer(int port, String dirUri){
        this.dirUri = dirUri;
        this.port = port;
        this.databaseManager = null;
        service = Service.ignite().port(port).threadPool(poolSize);
    }

    // Serve resources dir
    public HttpServer(int port){
        dirUri = null;
        databaseManager = null;
        this.port = port;
        service = Service.ignite().port(port).threadPool(poolSize);
    }

    public HttpServer(int port, DatabaseManager databaseManager){
        dirUri = null;
        this.databaseManager = databaseManager;
        this.port = port;
        service = Service.ignite().port(port).threadPool(poolSize);
    }

    public void run(){
        if(dirUri != null){
            service.staticFiles.externalLocation(dirUri);
        }
        else
            service.staticFiles.location("/public");
        service.init();
    }

    public HttpServer setWebSocket(Class<?> webSocketClass){
        service.webSocket("/websocket", webSocketClass);
        service.webSocketIdleTimeoutMillis(Integer.MAX_VALUE);
        return this;
    }

    public DatabaseManager getDBM(){
        return this.databaseManager;
    }
}
