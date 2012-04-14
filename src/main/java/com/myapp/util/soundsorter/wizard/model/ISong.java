package com.myapp.util.soundsorter.wizard.model;

import java.io.Serializable;


public interface ISong extends Serializable 
{

    public abstract String getTitle();

    public abstract String getArtist();

    public abstract String getAlbum();

    public abstract String getFile();

    public abstract int getLength();

    public abstract String getGenre();

    public abstract String getYear();

    public abstract int getTrack();

}