package com.myapp.games.schnellen.model;

import java.util.List;



public interface IGameContext {

    /**
     * helds the state of the current round. when the round was finished, the
     * state will remain until the next round has begun.
     * 
     * @return the state of the current round
     */
    public abstract IRound round();

    /**
     * answers a list of the player's names in the order of their playings.
     * 
     * @return the list of players. the dealer is always at position 0 in this
     *         view.
     */
    public abstract List<String> players();

    /**
     * helds the state about how and which color was determinded as the trupm
     * suit.
     * 
     * @return the color selector
     */
    public abstract IColors colors();

    /**
     * @return the current game's configuration
     */
    public abstract Config config();

    /**
     * answers the number of cards in the deck stack
     * 
     * @return the number of cards in the deck stack
     */
    public abstract int deckSize();

    /**
     * plays one round after the other, until one players has reached the target
     * score
     */
    public abstract void playGame();


    /**
     * returns the scoring component of the game.
     * 
     * @return the scorings of the game.
     */
    public IScorings scorings();
}