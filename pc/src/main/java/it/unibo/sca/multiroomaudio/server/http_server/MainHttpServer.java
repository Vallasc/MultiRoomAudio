package it.unibo.sca.multiroomaudio.server.http_server;

public class MainHttpServer extends HttpServer {

    public MainHttpServer(int port, ServerWebSocket webSocket) {
        super(port);
        super.setWebSocket(webSocket);
    }
    
    @Override
    public void run() {
        super.run();
        setRoutes();
    }

    public void setRoutes(){
        service.path("/offline", () -> {
            service.put("/start", (req, res) -> "{\"status\": \"OK\"}");
            service.put("/stop", (req, res) -> "{\"status\": \"OK\"}");
        });
    }
}
