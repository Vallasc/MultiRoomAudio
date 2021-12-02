package it.unibo.sca.multiroomaudio.server.http_server;

import com.google.gson.Gson;

import it.unibo.sca.multiroomaudio.server.DatabaseManager;
import it.unibo.sca.multiroomaudio.shared.messages.MsgOffline;

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
        Gson gson = new Gson();
        service.path("/offline", () -> {
            service.put("/start", (req, res) -> {
                //a start request is coming from a client
                MsgOffline msg = gson.fromJson(req.body(), MsgOffline.class);
                dbm.putRoom(msg.getRoom());
                if(dbm.getDeviceStart(msg.getId())) 
                    return "{\"status\": \"KO\"}";
                if(dbm.setDeviceStart(msg.getId(), msg.getRoom())){
                    return "{\"status\": \"OK\"}";
                }
                else
                    return "{\"status\": \"KO\"}";
            });
            service.put("/stop", (req, res) -> {
                MsgOffline msg = gson.fromJson(req.body(), MsgOffline.class);
                if(dbm.setDeviceStop(msg.getId())){
                    return "{\"status\": \"OK\"}";
                }
                else
                    return "{\"status\": \"KO\"}";
            });
        });
    }
}
