package com.myapp.games.schnellen.model;

import java.io.Serializable;
import java.util.List;


public interface IRound extends Serializable {

    /** determine if a player skips this round.
     * 
     * @param p
     *            the player
     * @return if the player skips */
    boolean skipsThisRound(String p);

    /** lists the cards played in this punch so far in their played
     * order.
     * 
     * @return the current punches' cards */
    List<Card> playedCards();

    /** determine the player that has played the last card in this
     * punch
     * 
     * @return the last player, or null if no card was played in this
     *         punch */
    String lastPlayer();

    /** determine the last played card in this punch
     * 
     * @return the last card played in this round */
    Card lastCard();

    /** lookup the number of punches for a player made in the current
     * round. number will be set to zero at the beginning of each
     * round.
     * 
     * @see Round#getScoreFactor()
     * @see Round#isGameFinal()
     * @param p
     *            the player
     * @return the number of punches made by this player */
    int getPunchCount(String p);

    /** if a player took the weli at "split deck", everybody knows that
     * he has the weli. this will be set to null before the beginning
     * of each round.
     * 
     * @return the player that has taken the weli during "split deck" */
    String getPublicWeliOwner();

    /** determines which player is currently the "owner" of the punch.
     * there may be other cards played. the highest player is
     * determined finally at the end of one punch.
     * 
     * @return the player that has played the highest card so far in
     *         this round, null if none. */
    String getHighestPlayer();

    /** query the number of cards that were exchanged by a player in
     * this round.
     * 
     * @param p
     *            the player
     * @return the number of cards that were redealt for the player */
    int getExchangeCount(String p);

    /** card dealers are handled special, they may say "bei mir" when
     * determining the punch count: they may name a punch promise
     * equal to the currently highest punch offer (when this game
     * variant is enabled)
     * 
     * @return the player who is the card dealer in this round. */
    String getDealer();

    /** determines if the given name is the dealer in the current
     * round.
     * 
     * @param name
     *            the name we want to test if it is the current
     *            dealer's
     * @return true when the given name refers to the dealer in the
     *         current round. */
    boolean isDealer(String name);

    /** returns a cardRules with the state of this round
     * 
     * @return cardRules with the state of this round */
    CardRules getCardRules();

}