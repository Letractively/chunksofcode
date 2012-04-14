package com.myapp.games.schnellen.model;

import java.io.Serializable;
import java.util.List;



public interface IGameContext extends Serializable {

    /**
     * helds the state of the current round. when the round was finished, the
     * state will remain until the next round has begun.
     * 
     * @return the state of the current round
     */
    IRound round();

    /**
     * answers a list of the player's names in the order of their playings.
     * 
     * @return the list of players. the dealer is always at position 0 in this
     *         view.
     */
    List<String> players();

    /**
     * helds the state about how and which color was determinded as the trupm
     * suit.
     * 
     * @return the color selector
     */
    IColors colors();

    /**
     * @return the current game's configuration
     */
    IConfig config();

    /**
     * answers the number of cards in the deck stack
     * 
     * @return the number of cards in the deck stack
     */
    int deckSize();

    /**
     * plays one round after the other, until one players has reached the target
     * score
     */
    void playGame();


    /**
     * returns the scoring component of the game.
     * 
     * @return the scorings of the game.
     */
    public IScorings scorings();
}