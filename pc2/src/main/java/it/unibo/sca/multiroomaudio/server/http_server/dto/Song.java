package it.unibo.sca.multiroomaudio.server.http_server.dto;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

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
    private String albumImageUrl = null;

    public Song(){}

    public Song setId(int id){
        this.id = id;
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

    public static Song fromMp3File(int id, Path filePath, File musicDir) 
                                        throws UnsupportedTagException, InvalidDataException, IOException{
        File songFile = filePath.toFile();
        Mp3File mp3file = new Mp3File(filePath);
        Song song = new Song();
        song.id = id;
        song.songUrl = songFile.toString()
                                .replace(musicDir.toString(), "")
                                .replace("\\", "/");
        //System.out.println("song url " + song.songUrl);
        song.songUrl = "./" + EncodingUtil.encodeURIComponent(song.songUrl);
        //System.out.println("song url2 " + song.songUrl);

        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            song.setArtist(id3v2Tag.getArtist())
                .setTitle(id3v2Tag.getTitle())
                .setAlbum(id3v2Tag.getAlbum())
                .setYear(id3v2Tag.getYear());

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
            song.setArtist(id3v1Tag.getArtist())
                .setTitle(id3v1Tag.getTitle())
                .setAlbum(id3v1Tag.getAlbum())
                .setYear(id3v1Tag.getYear());
        } else {
            song.setTitle(mp3file.getFilename());
        }
        return song;
    }

}
