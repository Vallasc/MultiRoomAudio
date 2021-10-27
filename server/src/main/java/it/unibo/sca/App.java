package it.unibo.sca;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {

        port(80);
        staticFiles.location("/public"); 
        webSocket("/client", ClientWebSocket.class);
        ClientRoutes.setRoutes();
        init();
    }

}