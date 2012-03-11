package com.myapp.mines.db;

public class HighscoreEntry {
    
    private final String name;
    private final long gameTime;
    
    public HighscoreEntry(String name, long gameTime) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be empty!");
        }
        if (gameTime < 0) {
            throw new IllegalArgumentException("gametime cannot be less than 0");
        }
        this.name = name;
        this.gameTime = gameTime;
    }
    
    public String getName() {
        return name;
    }
    public long getGameTime() {
        return gameTime;
    }
    
}
