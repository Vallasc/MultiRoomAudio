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
}
