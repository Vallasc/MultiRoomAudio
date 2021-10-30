package it.unibo.sca;

import static spark.Spark.*;

public class ClientRoutes {
    public static void setRoutes(){
        get("/hello", (req, res) -> "Hello World");
        get("/hello/:name", (request, response) -> {
            return "Hello: " + request.params(":name");
        });
    }
}
