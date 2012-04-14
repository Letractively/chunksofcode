package com.myapp.util.soundsorter.wizard.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDSong;

public class MPDAdapter implements IMetaDataSource {

    private MPD mpd;


    public MPDAdapter(MPD mpd) {
        this.mpd = mpd;
    }

    @Override
    public Collection<ISong> getSongsInDirectory(String pathPart) {
        Collection<ISong> songs = new ArrayList<ISong>();

        try {
            Collection<MPDSong> mpdSongs = mpd.getMPDDatabase().searchFileName(pathPart);
            
            for (Iterator<MPDSong> itr = mpdSongs.iterator(); itr.hasNext();) {
                MPDSong s = itr.next();
                ISong is = new Song(s);
                songs.add(is);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        return songs;
    }
}
