package com.myapp.games.schnellen.model;

import java.io.Serializable;




public interface IColors extends Serializable {

    /**
     * cards of the trump suit are higher than other cards (except special
     * cards)
     * 
     * @return the trump suit of the current round
     */
    Card.Color getTrumpSuit();

    /**
     * calculates the minimum offer number for that player to be the trump
     * speller
     *
     * @param p
     *            the player who wants to know the minimum punch count.
     * @return the minimum value to be the highest bid.
     */
    int getMinimumOfferValue(String p);

    /**
     * if the color was specified by a player (in game variant:
     * "determine color by punch promise") , this will return the player that
     * set the trump suit in this round. in game variant
     * "determine color by first card after initial deal" this will return null<br>
     * the value of the punch promise can be requestet by
     * {@link Round#getPunchPromise()}
     *
     * @return the player that had spelled the trump suit, or that is currently
     *         the one with the highest promise. null if the color was not
     *         chosen by a player or no offer was made yet
     */
    String getSpeller();

    /**
     * if the color was specified by a player (in game variant:
     * "determine color by punch promise") , this will return the number of
     * punches that were promised by the trump speller. in game variant
     * "determine color by first card after initial deal" this will return -1<br>
     * request the trump speller via: {@link Round#getTrumpSpeller()}
     *
     * @return the number of punches the trump speller had said.
     */
    int getSpellersPromise();

    /**
     * when the color was determined by uncovering the first card after dealing,
     * this links to the card, it is visible to all players.
     * @return
     */
    Card uncoveredCard();

}