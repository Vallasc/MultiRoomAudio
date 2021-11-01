package it.unibo.sca.multiroomaudio.server;

import spark.Service;

public class HttpServer extends Thread {
    private final String dirUrl;
    private final Service service;
    private final int port;
    private final int poolSize = 8;

    public HttpServer(int port, String dirUrl){
        this.dirUrl = dirUrl;
        this.port = port;
        service = Service.ignite().port(port).threadPool(poolSize);
    }

    // Serve resources dir
    public HttpServer(int port){
        dirUrl = null;
        this.port = port;
        service = Service.ignite().port(port).threadPool(poolSize);
    }

    public void run(){
        if(dirUrl != null)
            service.staticFiles.externalLocation(dirUrl);
        else
            service.staticFiles.location("/public");
        service.init();
    }
    
}
