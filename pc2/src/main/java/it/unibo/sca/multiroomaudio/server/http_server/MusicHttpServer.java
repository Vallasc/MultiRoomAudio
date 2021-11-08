package it.unibo.sca.multiroomaudio.server.http_server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.Gson;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;
import it.unibo.sca.multiroomaudio.shared.messages.MsgPlay;

// Only mp3 format supported
public class MusicHttpServer extends HttpServer {
    private File dir;
    private ArrayList<Song> songs;
    private Gson gson = new Gson();

    public MusicHttpServer(int port, String dirUri){
        super(port, dirUri);
        dir = new File(dirUri);
        songs = new ArrayList<>();
    }
    
    @Override
    public void run() {
        super.run();
        setRoutes();
    }

    public void setRoutes(){
        service.path("/player", () -> {
            service.get("/play/:id", (req, res) -> {
                System.out.println("PLAY id: " + req.params(":id"));
                try{
                    ServerWebSocket.sendAll(new MsgPlay(songs, Integer.parseInt(req.params(":id")), 0));
                    return "{\"status\": \"OK\"}";
                } catch (NumberFormatException e){
                    return "{\"status\": \"KO\"}";
                }
            });
            service.put("/pause", (req, res) -> "{\"status\": \"OK\"}");
            service.put("/stop", (req, res) -> "{\"status\": \"OK\"}");
            service.get("/list", (req, res) -> songs, gson::toJson);
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

    public MusicHttpServer listMusic() throws IOException{
        songs.clear();
        System.out.println("Songs:");
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
            private int index = 0;

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    Optional<String> ext = getExtension(file.toString());
                    //(ext.get() == ".mp3" || ext.get() == ".MP3")
                    if(ext.isPresent() && (ext.get().equals("mp3") || ext.get().equals("MP3"))) {
                        songs.add(Song.fromMp3File(index++, file, dir));
                        System.out.println("- " + file);
                    }
                } catch (UnsupportedTagException | InvalidDataException | IOException e) {
                    e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return this;
    }

    public Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
