package com.myapp.util.soundsorter.wizard.tool;

public interface IAudioPlayer {
    void play(String lookupPath, long offset);
    void stop();
    void jump(long offset);
    long getElapsedTime();
    long getCurrentSongDuration();
    boolean isPlaying();
}
