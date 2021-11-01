package it.unibo.sca.multiroomaudio.server.http_server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;

// Only mp3 format supported
public class MusicHttpServer extends HttpServer {
    private File dir;
    private List<Song> songList;


    public MusicHttpServer(int port, String dirUri){
        super(port, dirUri);
        dir = new File(dirUri);
        songList = new LinkedList<>();
    }
    
    public void run(){
        super.run();
        setRoutes();
        try {
            listMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRoutes(){
        service.path("/player", () -> {
            service.put("/play", (req, res) -> "Hello World");
            service.put("/pause", (req, res) -> "Hello World");
            service.put("/stop", (req, res) -> "Hello World");
            service.get("/list", (req, res) -> "Hello World");
        });
    }

    public void listMusic() throws IOException{
        songList.clear();
        System.out.println("Songs:");
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    songList.add(Song.fromMp3File(file, dir));
                    System.out.println(" - " + file);
                } catch (UnsupportedTagException | InvalidDataException | IOException e) {
                    //e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
