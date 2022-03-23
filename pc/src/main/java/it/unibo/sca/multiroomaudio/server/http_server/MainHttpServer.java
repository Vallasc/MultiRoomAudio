package it.unibo.sca.multiroomaudio.server.http_server;

/**
 * Main HTTP server that serve web app
 */
public class MainHttpServer extends HttpServer {
    public MainHttpServer(int port, ServerWebSocket webSocket) {
        super(port);
        super.setWebSocket(webSocket);
    }
}
