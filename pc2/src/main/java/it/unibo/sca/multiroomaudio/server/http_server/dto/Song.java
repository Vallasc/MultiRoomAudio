package it.unibo.sca.multiroomaudio.server.http_server.dto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Song {
    private String track = null;
    private String artist = null;
    private String title = null;
    private String album = null;
    private String year = null;
    private final String songUrl;

    public Song(String songUrl){
        this.songUrl = songUrl;
    }

    public Song setTrack(String track){
        this.track = track;
        return this;
    }

    public Song setArtist(String artist){
        this.artist = artist;
        return this;
    }

    public Song setTitle(String title){
        this.title = title;
        return this;
    }

    public Song setAlbum(String album){
        this.album = album;
        return this;
    }

    public Song setYear(String year){
        this.year = year;
        return this;
    }

    public String getTrack(){
        return this.track;
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
    public static Song fromMp3File(Path filePath, File musicDir) 
                                        throws UnsupportedTagException, InvalidDataException, IOException{
        Mp3File mp3file = new Mp3File(filePath);

        String pathDir = musicDir.getPath();
        String pathFile = filePath.toString();
        System.out.println(pathDir);
        System.out.println(pathFile);

        Song song = new Song(filePath.toString()
                                .replace(musicDir.getPath(), ".")
                                .replace("\\", "/"));
        System.out.println(song.getSongUrl());
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            song.setTrack(id3v2Tag.getTrack())
                .setArtist(id3v2Tag.getArtist())
                .setTitle(id3v2Tag.getTitle())
                .setAlbum(id3v2Tag.getAlbum())
                .setYear(id3v2Tag.getYear());

            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                RandomAccessFile file;
                try {
                    file = new RandomAccessFile("album-artwork", "rw");
                    file.write(imageData);
                    file.close();
                } catch ( IOException e) {
                    //e.printStackTrace();
                }
            }
        } else if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            song.setTrack(id3v1Tag.getTrack())
                .setArtist(id3v1Tag.getArtist())
                .setTitle(id3v1Tag.getTitle())
                .setAlbum(id3v1Tag.getAlbum())
                .setYear(id3v1Tag.getYear());
        } else {
            song.setTrack(mp3file.getFilename())
                .setTitle(mp3file.getFilename());
        }
        return song;
    }

}
