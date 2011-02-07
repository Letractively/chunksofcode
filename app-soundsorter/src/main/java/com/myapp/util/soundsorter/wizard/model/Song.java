package com.myapp.util.soundsorter.wizard.model;

import java.io.Serializable;

import org.bff.javampd.MPDSong;

/**Serializable {@link MPDSong} wrapper*/
class Song implements ISong, Serializable {
    
    private static final long serialVersionUID = 3263657963112559984L;
    
    private String  title       = null;
    private String  artist      = null;
    private String  album       = null;
    private String  file        = null;
    private String  genre       = null;
    private String  comment     = null;
    private String  year        = null;
    private int     length      = 0;
    private int     track       = 0;
    private int     position    = -1;
    private int     id          = -1;

    
    
    public Song(MPDSong delegate) {
        this.title    = delegate.getTitle();
        this.artist   = delegate.getArtist();
        this.album    = delegate.getAlbum();
        this.file     = delegate.getFile();
        this.genre    = delegate.getGenre();
        this.comment  = delegate.getComment();
        this.year     = delegate.getYear();
        this.length   = delegate.getLength();
        this.track    = delegate.getTrack();
        this.position = delegate.getPosition();
        this.id       = delegate.getId();
    }
    
    public Song(String title,
                String artist,
                String album,
                String file,
                String genre,
                String comment,
                String year,
                int length,
                int track,
                int position,
                int id) {
        this.title    = title   ;
        this.artist   = artist  ;
        this.album    = album   ;
        this.file     = file    ;
        this.genre    = genre   ;
        this.comment  = comment ;
        this.year     = year    ;
        this.length   = length  ;
        this.track    = track   ;
        this.position = position;
        this.id       = id      ;
    }

    public Song() {
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("File:" + getFile() + "\n");
        sb.append("Title:" + getTitle() + "\n");
        sb.append("Artist:" + getArtist() + "\n");
        sb.append("Album:" + getAlbum() + "\n");
        sb.append("Track:" + getTrack() + "\n");
        sb.append("Year:" + getYear() + "\n");
        sb.append("Genre:" + getGenre() + "\n");
        sb.append("Comment:" + getComment() + "\n");
        sb.append("Length:" + getLength() + "\n");
        sb.append("Pos:" + getPosition() + "\n");
        sb.append("SongId:" + getId() + "\n");

        return (sb.toString());
    }

    public String getComment() {
        return comment;
    }

    public int getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getAlbum() {
        return album;
    }

    @Override
    public String getFile() {
        return file;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public String getYear() {
        return year;
    }

    public int getLength() {
        return length;
    }

    @Override
    public int getTrack() {
        return track;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setArtist(String artist) {
        this.artist = artist;
    }

    protected void setAlbum(String album) {
        this.album = album;
    }

    protected void setFile(String file) {
        this.file = file;
    }

    protected void setGenre(String genre) {
        this.genre = genre;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

    protected void setYear(String year) {
        this.year = year;
    }

    protected void setLength(int length) {
        this.length = length;
    }

    protected void setTrack(int track) {
        this.track = track;
    }

    protected void setPosition(int position) {
        this.position = position;
    }

    protected void setId(int id) {
        this.id = id;
    }
}
