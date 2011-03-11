package com.myapp.games.schnellen.model;

import java.util.LinkedHashMap;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;

public final class GameFactory {

    private LinkedHashMap<String, IPlayerFrontend> players = new LinkedHashMap<String, IPlayerFrontend>();
    private IConfig config = null;

    
    public GameFactory() {}

    
    /**
     * adds a player to the game
     * 
     * @param frontend
     *            the player frontend that represents the player
     * @return an error message when the player could not be registered
     */
    public String putPlayer(IPlayerFrontend frontend) {
        String uniqueTest = frontend.getName().trim().toLowerCase();
        
        if (players.containsKey(uniqueTest)) {
            return "player "+uniqueTest+" is already registered in the game!";
        }

        players.put(uniqueTest, frontend);
        return null;
    }
    
    /**
     * sets the configuration to be used by the game
     * 
     * @param config
     */
    public void setConfig(IConfig config) {
        this.config = config;
    }
    
    /**
     * answers the number of players registered yet.
     * 
     * @return the number of players registered yet.
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * creates a new game with the given parameters
     * 
     * @return the new game
     */
    public IGameContext createGame() {
        if (players.size() < 2)
            throw new IllegalStateException("must at least contain 2 players:" + players.keySet());

        if (config == null)
            config = new Config();

        Game game = new Game(config);

        for (IPlayerFrontend pf : players.values())
            game.addPlayer(pf);

        players.clear();
        config = null;
        
        return game;
    }
}