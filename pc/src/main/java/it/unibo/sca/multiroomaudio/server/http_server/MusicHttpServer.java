package it.unibo.sca.multiroomaudio.server.http_server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

import com.google.gson.Gson;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import it.unibo.sca.multiroomaudio.server.MusicOrchestrationManager;
import it.unibo.sca.multiroomaudio.server.http_server.dto.Song;

// Only mp3 format supported
public class MusicHttpServer extends HttpServer {
    private File dir;
    private ArrayList<Song> songs;
    private Gson gson = new Gson();
    private MusicOrchestrationManager musicManager;

    public MusicHttpServer(int port, String dirUri, MusicOrchestrationManager musicManager){
        super(port, dirUri);
        dir = new File(dirUri);
        songs = new ArrayList<>();
        this.musicManager = musicManager;
    }
    
    @Override
    public void run() {
        super.run();
        setRoutes();
        setStaticFilesHeader();
    }

    public void setRoutes(){
        service.get("/songs", (req, res) -> songs, gson::toJson);
    }

    public void setStaticFilesHeader(){
        service.staticFiles.header("Access-Control-Allow-Origin", "*");
    }

    public MusicHttpServer listMusic() throws IOException{
        songs.clear();
        System.out.println("Songs:");
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                try {
                    Optional<String> ext = getExtension(file.toString());
                    if(ext.isPresent() && (ext.get().equals("mp3") || ext.get().equals("MP3"))) {
                        songs.add(Song.fromMp3File(0, file, dir));
                        System.out.println("- " + file);
                    }
                } catch (UnsupportedTagException | InvalidDataException | IOException e) {
                    e.printStackTrace();
                }
                return FileVisitResult.CONTINUE;
            }
        });
        Collator collator = Collator.getInstance();
        songs.sort( new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2){
                return collator.compare(s1.getFilePath(), s2.getFilePath());
            }
        });

        // Set song id
        for(int songIndex = 0; songIndex< songs.size(); songIndex++)
            songs.get(songIndex).setId(songIndex);

        musicManager.setMusicList(songs);
        return this;
    }

    public Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
