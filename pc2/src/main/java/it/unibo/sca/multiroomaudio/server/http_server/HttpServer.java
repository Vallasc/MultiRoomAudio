package it.unibo.sca.multiroomaudio.server.http_server;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import spark.Service;

public class HttpServer extends Thread {
    protected final String dirUri;
    protected final Service service;
    private final int port;
    private final int poolSize = 8;


    public HttpServer(int port, String dirUri){
        this.dirUri = dirUri;
        this.port = port;
        service = Service.ignite().port(port).threadPool(poolSize);
    }

    // Serve resources dir
    public HttpServer(int port){
        dirUri = null;
        this.port = port;
        service = Service.ignite().port(port).threadPool(poolSize);
        
    }

    public void setRoutes(){
        service.path("/offline", () -> {
            // /offline/start?id=pippo
            service.put("/start", (req, res) -> "{\"status\": \"OK\"}");
            service.put("/stop", (req, res) -> "{\"status\": \"OK\"}");
        });

        // Enable CORS
        service.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        service.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    public void run(){
        System.out.println("RUNNING");
        if(dirUri != null){
            service.staticFiles.externalLocation(dirUri);
        }
        else
            service.staticFiles.location("/public");
        service.init();
        setRoutes();
    }

    public void setRoutes(){
        service.path("/offline", () -> {
            service.put("/start", (req, res) -> "{\"status\": \"OK\"}");
            service.put("/stop", (req, res) -> "{\"status\": \"OK\"}");
        });

        // Enable CORS
        service.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
        service.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    public HttpServer setWebSocket(Class<?> webSocketClass){
        service.webSocket("/websocket", webSocketClass);
        service.webSocketIdleTimeoutMillis(Integer.MAX_VALUE);
        return this;
    }

    public HttpServer setWebSocket(ServerWebSocket handler){
        service.webSocket("/websocket", handler);
        service.webSocketIdleTimeoutMillis(Integer.MAX_VALUE);
        return this;
    }
}
