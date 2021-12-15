package it.unibo.sca.multiroomaudio.server.http_server.dto;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import it.unibo.sca.multiroomaudio.server.http_server.EncodingUtil;

public class Song {
    private int id;
    private String artist = null;
    private String title = null;
    private String album = null;
    private String year = null;
    private String songUrl = null;
    private String dirPath = null;
    private String fileName = null;
    private String filePath = null;
    private String albumImageUrl = null;
    private long durationMs = 0;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public String getArtist(){
        return this.artist;
    }

    public String getTitle(){
        return this.title;
    }

    public String getAlbum(){
        return this.album;
    }

    public String getYear(){
        return this.year;
    }

    public String getSongUrl(){
        return songUrl;
    }

    public long getDuration(){
        return durationMs;
    }

    public float getDurationSec(){
        return durationMs/1000;
    }

    public String getDir(){
        return dirPath;
    }

    public String getFilePath(){
        return filePath;
    }

    public String getFileName(){
        return fileName;
    }

    public static Song fromMp3File(int id, Path filePath, File musicDir) 
                                        throws UnsupportedTagException, InvalidDataException, IOException{
        File songFile = filePath.toFile();
        Mp3File mp3file = new Mp3File(filePath);
        Song song = new Song();
        song.id = id;
        song.filePath = songFile.toString()
                                .replace(musicDir.toString(), "")
                                .replace("\\", "/");
        song.songUrl = "./" + EncodingUtil.encodeURIComponent(song.filePath);
        song.durationMs = mp3file.getLengthInMilliseconds();

        Path p = Paths.get(song.filePath);
        String fileName = p.getFileName().toString();
        song.fileName = fileName;
        song.dirPath = song.filePath.replace(fileName, "");
        song.dirPath = song.dirPath.replaceAll("^\\\\+", ""); //TODO Testing
        song.dirPath = song.dirPath.replaceAll("^\\/+", "");
        song.dirPath = song.dirPath.replaceAll("\\\\+$", "");
        song.dirPath = song.dirPath.replaceAll("\\/+$", "");

        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            song.artist = id3v2Tag.getArtist();
            song.title = id3v2Tag.getTitle();
            song.album = id3v2Tag.getAlbum();
            song.year = id3v2Tag.getYear();

            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                File coverDir = new File(musicDir.getPath() + File.separator + "mra_tmp");
                coverDir.mkdir();
                File cover = new File(coverDir.getPath() + File.separator + 
                                        song.getAlbum() + "_"  + song.getArtist() + ".jpeg" );
                RandomAccessFile file;
                try {
                    file = new RandomAccessFile(cover, "rw");
                    file.write(imageData);
                    file.close();
                    song.albumImageUrl = cover.toString()
                                                .replace(musicDir.toString(), "")
                                                .replace("\\", "/");
                    song.albumImageUrl = "./" + EncodingUtil.encodeURIComponent(song.albumImageUrl);
                } catch ( IOException e) {
                    //e.printStackTrace();
                }
            }
        } else if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            song.artist = id3v1Tag.getArtist();
            song.title = id3v1Tag.getTitle();
            song.album = id3v1Tag.getAlbum();
            song.year = id3v1Tag.getYear();
        } else {
            song.title = song.fileName;
        }
        return song;
    }

}
