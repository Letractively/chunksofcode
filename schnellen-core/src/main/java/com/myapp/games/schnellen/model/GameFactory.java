package com.myapp.games.schnellen.model;

import java.util.LinkedHashMap;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;

public final class GameFactory {

    private LinkedHashMap<String, IPlayerFrontend> players
                                 = new LinkedHashMap<String, IPlayerFrontend>();

    public GameFactory() {}

    public void putPlayer(IPlayerFrontend frontend) {
        String uniqueTest = frontend.getName().trim().toLowerCase();
        
        if (players.containsKey(uniqueTest))
            throw new RuntimeException("player "+frontend.getName().trim().toLowerCase()+" is already contained in the game!");

        players.put(uniqueTest, frontend);
    }

    public IGameContext createGame() {
        if (players.size() < 2)
            throw new IllegalStateException("must at least contain 2 players:" + players.keySet());

        Game game = new Game();

        for (IPlayerFrontend pf : players.values())
            game.addPlayer(pf);

        players.clear();
        return game;
    }
}