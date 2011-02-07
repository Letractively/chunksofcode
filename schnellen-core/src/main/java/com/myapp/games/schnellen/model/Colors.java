package com.myapp.games.schnellen.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.myapp.games.schnellen.frontend.IPlayerFrontend.Event;
import com.myapp.games.schnellen.model.Card.Color;

/**
 * selects the trump suit for the next round.
 * there are two variations:
 * either the players determine the trump suit by offering punch counts,
 * or the first card after dealing determines the card.
 *
 * @author andre
 */
public final class Colors implements IColors {

    private final Game context;

    private Color trump = null;
    private String colorChooser = null;
    private int promise = -1;
    private Card uncoveredCard = null;

    public Colors(Game game) {
        context = game;
    }


    /**
     * cards of the trump suit are higher than other cards (except special
     * cards)
     * 
     * @return the trump suit of the current round
     */
    @Override
    public Color getTrumpSuit() {
        return trump;
    }

    /**
     * calculates the minimum offer number for that player to be the trump
     * speller
     *
     * @param p
     *            the player who wants to know the minimum punch count.
     * @return the minimum value to be the highest bid.
     */
    @Override
    public int getMinimumOfferValue(String p) {
        if (promise < 2)
            return 2;
        if (p.equals(context.round().getDealer())
                     && Config.getInstance().dealerMaySayBeiMir())
            return promise;
        return promise + 1;
    }

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
    @Override
    public String getSpeller() {
        return colorChooser;
    }


    /**
     * if the color was specified by a player (in game variant:
     * "determine color by punch promise") , this will return the number of
     * punches that were promised by the trump speller. in game variant
     * "determine color by first card after initial deal" this will return -1<br>
     * request the trump speller via: {@link Round#getTrumpSpeller()}
     *
     * @return the number of punches the trump speller had said.
     */
    @Override
    public int getSpellersPromise() {
        return promise;
    }

    /**
     * when the color was determined by uncovering the first card after dealing,
     * this links to the card, it is visible to all players.
     * @return
     */
    @Override
    public Card uncoveredCard() {
        return uncoveredCard;
    }

    /**
     * a color will be announced. cards in the announced colors are higher than
     * the others.<br>
     * player after player, (the player next to the dealer first) may name a
     * number of punches he thinks to make. he may also skip without telling a
     * number.<br>
     * <br>
     * the dealer may say an equal high number. (when a player skips, he must
     * not name a number again this time.)<br>
     * <br>
     * if nobody wants to name a number, the colors will be shuffled and new
     * cards will be dealt.<br>
     * the player who names the highest number of punches will announce the
     * color for this round.<br>
     *
     * @return the announced color, or null, if no one said a number.
     */
    Color determineColor() {
        initForNewRound();
        Color c = null;

        if (Config.getInstance().isTrumpDeterminedByPunchOffering())
            c = determineColorByPunchOffering();
        else
            c = determineColorByNextCard();

        return c;
    }

    private Card.Color determineColorByNextCard() {
        uncoveredCard = context.uncoverNextCardForColorChoosing();
        return uncoveredCard.getColor();
    }


    private Card.Color determineColorByPunchOffering() {
        List<String> players = context.players();
        Set<String> left = new HashSet<String>(players);

        for (int i = 1, s = players.size(); ; i = (i+1) % s) {
            if (left.isEmpty())
                break;

            String p = players.get(i);

            if ( ! left.contains(p))
                continue;

            if (p.equals(colorChooser)) // one round with no other offer
                break;

            int offer = context.frontend(p).offerPunch();
            offerPunchCount(p, offer);

            if (! p.equals(colorChooser)) // player chose to skip
                left.remove(p);

            if (5 < promise)
                break;
        }

        if (left.isEmpty())
            return null;

        Card.Color announcedColor = context.frontend(colorChooser).spellTrumpSuit();
        if (announcedColor == null) {
            throw new RuntimeException("no color was chosen!");
        }
        return announcedColor;
    }

    private void offerPunchCount(String player, int offer) {
        if (offer < 2 || promise >= 5)
            return;

        int count = offer > 5 ? 5 : offer;

        if (count >= getMinimumOfferValue(player)) {
            colorChooser = player;
            promise = offer;
        }
    }

    void initForNewRound() {
        promise = -1;
        colorChooser = null;
        uncoveredCard = null;
        trump = null;
    }
    
    final void setTrump(Color trump) {
        this.trump = trump;

        if (this.trump == null)
            return;
        
        String highest;
        
        if (colorChooser != null) {
            highest = colorChooser;
        } else {
            highest = context.players().get(1);  // next to dealer
        }
        
        context.getRound2().setHighestPlayer(highest);
        context.fireGlobalEvent(Event.TRUMP_SUIT_DETERMINED);
    }
}
