package com.myapp.util.soundsorter.wizard.tool;

import java.util.Collection;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;
import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.MPDSong;
import org.bff.javampd.exception.MPDException;


public class MPDAudioPlayer implements IAudioPlayer {
    
    private MPD mpd;

    
//    public MPDAudioPlayer() {
//        Config cfg = Config.getInstance();
//        String host = cfg.getProperty(Config.PROPKEY_MPD_SERVER_HOSTNAME);
//        String port = cfg.getProperty(Config.PROPKEY_MPD_SERVER_PORT);
//        int portNum = Integer.parseInt(port);
//        
//        try {
//            mpd = new MPD(host, portNum);
//            
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


    public MPDAudioPlayer(MPD mpd) {
        this.mpd = mpd;
    }

    
    public void play(String lookupPath, long offset) {
        try {
            Collection<MPDSong> songs = mpd.getMPDDatabase().searchFileName(lookupPath);
            
            if (songs.size() <= 0) {
                throw new RuntimeException("no such song: " + lookupPath);
            }
            if (songs.size() > 1) {
                throw new RuntimeException("multiple songs found: " + lookupPath + "\n" + songs);
            }
            
            MPDSong currentSong  = songs.toArray(new MPDSong[1])[0];
//            System.out.println("MPDAudioPlayer.play() will now play:\n" + currentSong);

            MPDPlayer player = mpd.getMPDPlayer();
            player.stop();
            
            MPDPlaylist playlist = mpd.getMPDPlaylist();
            playlist.clearPlaylist();
            playlist.addSong(currentSong);
            player.play();
            
            if (offset != 0) {
                player.seek(offset);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        MPDPlayer player = mpd.getMPDPlayer();
        
        try {
            player.pause();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public void jump(long offset) {
        long total = getCurrentSongDuration();
        long current = getElapsedTime();
        long jumpTo = 0L;
        
        if ((current + offset) > total) {
            stop();
            
        } else if ((current + offset) < 0) {
            stop();
        
        } else {
            jumpTo = current + offset;
        }

        MPDPlayer player = mpd.getMPDPlayer();
        
        try {
            player.seek(jumpTo);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public long getElapsedTime() {
        MPDPlayer player = mpd.getMPDPlayer();
        
        try {
            return player.getElapsedTime();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public long getCurrentSongDuration() {
        MPDPlayer player = mpd.getMPDPlayer();
        
        try {
            return player.getCurrentSong().getLength();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public boolean isPlaying() {
        MPDPlayer player = mpd.getMPDPlayer();
        
        try {
            return player.getStatus() == PlayerStatus.STATUS_PLAYING;
            
        } catch (MPDException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
