package com.myapp.games.schnellen.model;

import static java.util.Collections.disjoint;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * responsible for comparisons of cards during gameplay. cardrules decide which
 * cards are currently possible to play and which card is highest in a punch.<br>
 * 
 * <b>card comparison rules:</b><br>
 * 
 * usually, cards with the <b>same color</b> are compared by their value. (equal
 * to comparing them by their int value)<br>
 * usually means that there are two color constraints depending on the current
 * game's state (trump suit, the color of the first card played out in the
 * current punch).<br>
 * 
 * there are also one or two special cards {@link Card#WELI} and
 * {@link Card#PAPA}, where the second may be disabled depending on game
 * variations. the weli card is always the second-highest card in the game,
 * beaten only by the sau of the current trump color.<br>
 * in game variant "papa is highest", the {@link Value#koenig} in the color
 * {@link Color#herz} is higher than any other card in the game.<br>
 * 
 * <b>except</b> these "special cards", the cards are ordered by their colors
 * depending of the state of the game:
 * <ul>
 * <li>the <b>current trump suit</b>: cards of the same color as the current
 * game's trump suit are higher than cards with a different color</li>
 * <li>the color of the <b>first card of a punch</b>: cards of this color are
 * higher than cards with a different and non-trump color</li>
 * </ul>
 * 
 * @author andre
 */
public final class CardRules implements Serializable {
    
    private static final long serialVersionUID = 6331091364260984152L;
    
    private List<Card> playedCards = new Card.CardList();
    private Card.Color trumpSuit = null;

    public CardRules() {}

    /**
     * determines the set of cards that are possible to play in the current game
     * punch.
     *
     * @see CardRules#calcPossibleCards(List, Card, Color)
     * @return the set of cards that are possible to play
     */
    public List<Card> calcPossibleCards(List<Card> hand) {
        return calcPossibleCards(playedCards, hand, trumpSuit);
    }

    /**
     * tests if a card would be the highest card after adding it to the current
     * punch.<br>
     * 
     * @see CardRules#punches(List, Card, Color)
     * @return true when the card is higher than the others
     */
    public boolean punches(Card card) {
        return punches(playedCards, card, trumpSuit);
    }

    public void setPlayedCards(List<Card> playedCards) {
        this.playedCards.clear();
        this.playedCards.addAll(playedCards);
    }

    public void setTrumpSuit(Card.Color c) {
        trumpSuit = c;
    }
    
    public void reset() {
        trumpSuit = null;
        playedCards.clear();
    }

    
    
    
    

    /**
     * if (the player has no picture cards or three number-valued cards of the
     * same value ) AND the player wants to redeal, he can say he is unterm
     * hund. this means that the cards will be redealt. the weli is treated as a
     * number card.<br>
     * special cards like the "papa" or the "weli" will be kept by their lucky
     * owner(s) when redealing.
     * 
     * @param hand
     *            the cards in the hand
     * @return if he is technically unterm hund.
     */
    public static boolean isTechnicallyUntermHund(List<Card> hand) {
        assert hand.size() == 5 : hand; // called only after cards exchange
        boolean havePictureCards = false;
        boolean haveThreeEqualNumberCards = false;
        Map<Card.Value, Integer> valueSpread = CardRules.valueSpread(hand);

        for (Card.Value cardVal : valueSpread.keySet()) {
            if (Card.isPictureValue(cardVal))
                havePictureCards = true;
            else if (valueSpread.get(cardVal) >= 3)
                haveThreeEqualNumberCards = true;
        }
        
        if (! havePictureCards || haveThreeEqualNumberCards)
            return true;

        return false;
    }
    
    




    /**
     * maps the count of cards to each different color occuring in the given set
     * of cards. the weli card will be omitted in this calculation, since it has
     * no color.
     *
     * @param cards
     *            the cards to create the mapping for
     * @return a mapping from the occuring colors to the number of its cards
     */
    public static final Map<Card.Color, Integer> colorSpread(
                                                       Collection<Card> cards) {
        Map<Card.Color, Integer> spread = new HashMap<Card.Color, Integer>();

        for (Card c : cards) {
            Card.Color key = c.getColor();

            if (c.equals(Card.WELI)) {
                assert key == null : key;
                continue;
            }

            Integer cnt = spread.get(key);
            spread.put(key, cnt == null ? 1 : (1 + cnt));
        }

        return spread;
    }

    /**
     * maps the count of cards to each different card value occuring in the
     * given set of cards. the weli card will be omitted in this calculation,
     * since it has no color.
     * 
     * @param cards
     *            the cards to create the mapping for
     * @return a mapping from the occuring colors to the number of its cards
     */
    public static final Map<Card.Value, Integer> valueSpread(
                                                       Collection<Card> cards) {
        Map<Card.Value, Integer> spread = new HashMap<Card.Value, Integer>();

        for (Card c : cards) {
            Card.Value key = c.getValue();
            if (c.equals(Card.WELI))
                continue;
            Integer cnt = spread.get(key);
            spread.put(key, cnt == null ? 1 : (1 + cnt));
        }

        return spread;
    }
    

    /**
     * determines the set of cards that are possible to play. it depends on the
     * cards played, the set of cards available and the current trump suit. <br>
     * <br>
     * if there were already one or more cards played in one punch, and the
     * player has any cards in the first played card's color, he has to play one
     * of these. else the player may play any cards in his hand.
     * 
     * @param played
     *            the set of cards played in this round.
     * @param hand
     *            the available cards where the calculation is applied to.
     * @param trump
     *            this parameter is currently not influencing the result, but
     *            there may be game variations where the possible cards set in a
     *            players hand may depend on the current trump suit in the
     *            future.
     * @return a set of cards that are legal to play in the current punches'
     *         state.
     */
    public static List<Card> calcPossibleCards(List<Card> played,
                                               List<Card> hand,
                                               Card.Color trump) {
        if (hand == null || hand.isEmpty() || trump == null)
            throw new RuntimeException("hand="+hand+", trump="+trump);
        if (played == null || played.isEmpty() || hand.size() == 1)
            return hand; // all possible
        assert disjoint(played, hand) : "hand="+hand+", played="+played;

        Card first = played.get(0);

        if (first.equals(Card.WELI))
            return hand; // all possible

        Card.Color firstColor = first.getColor();
        List<Card> possible = null;

        for (Card c : hand)
            if (c.getColor() == firstColor || c.equals(Card.WELI)) {
                if (possible == null)
                    possible = new Card.CardList();
                possible.add(c);
            }

        if (possible == null || 
                      (possible.size() == 1 && possible.contains(Card.WELI))) {
            return hand; // no cards of the 1st card's color: may play any
        }
        
        return possible;
    }
    
    private static boolean contains(Collection<Card> coll, Card.Color c, Card.Value v) {
        for (Iterator<Card> itr = coll.iterator(); itr.hasNext();) {
            Card card = itr.next();
            if (card.getColor() == c && card.getValue() == v)
                return true;
        }
        return false;
    }


    public static final Map<Card.Color, Integer> pointsPerColor(Collection<Card> cards) {
        Map<Card.Color, Integer> ppc = new HashMap<Card.Color, Integer>();

        for (Card c : cards) {
            if (c.equals(Card.WELI))
                continue;

            Card.Color key = c.getColor();
            Integer sum = ppc.get(key);
            int cardIntVal = c.getValue().intValue();

            ppc.put(key, cardIntVal + (sum != null ? sum : 0));
        }

        return ppc;
    }


    /**
     * tests if a card would be the highest card after adding it to the current
     * punch. the player of this card will "own" the punch until an other player
     * punches again.
     * 
     * @param played
     *            the list of cards played so far (null will be treated as an
     *            empty card stack where every card c would be the highest. the
     *            list will not be modified)
     * @param card
     *            the card where to test if it would be the highest one in the
     *            given stack
     * @param trumpSuit
     *            the current trump suit, must not be null
     * @return true if this card would punch the other cards
     */
    public static boolean punches(final List<Card> played,
                                  final Card card,
                                  final Card.Color trumpSuit) {
        if (played == null || played.isEmpty())
            return true;

        assert trumpSuit != null;
        assert ! played.contains(card);

        //////////////////// special cases: ////////////////////////////
        // only sau in trump suit (and papa, if enabled) punches weli
        final Card.Color cardCol = card.getColor();
        final Card.Value val = card.getValue();

        // nobody can beat papa
        if (card.equals(Card.PAPA) && card.isSpecialCard())
            return true;
        
        int papaIndex = played.indexOf(Card.PAPA);
        if (papaIndex >= 0 && played.get(papaIndex).isSpecialCard())
            return false;
        
        if (played.contains(Card.WELI))
            return val == Card.Value.sau && cardCol == trumpSuit;
        if (card.equals(Card.WELI))
            return ! contains(played, trumpSuit, Card.Value.sau);

        //////////////////// regular comparison: ////////////////////////////
        Card first = played.get(0);
        final Card.Color firstCol = first.getColor();
        
        if (cardCol != firstCol && cardCol != trumpSuit)
            return false; // color punch: card different than 1st and not trump

        for (Card ithCard : played) {
            Card.Color ithColor = ithCard.getColor();

            // color punches:
            if (cardCol != trumpSuit) {
                if (ithColor == firstCol && cardCol != firstCol)
                    return false; // ith has 1st, card neither 1st nor trump
                if (ithColor == trumpSuit)
                    return false; // ith in trump, card not

            } else if (ithColor != trumpSuit)
                continue; // don't need to determine value, this color is higher

            // value punch:
            if (cardCol == ithColor && ithCard.getValue().compareTo(val) > 0)
                return false; // same color, ith higher
        }

        return true;
    }

    public static Card.Color suggestColor(List<Card> hand) {
        Map<Card.Color, Integer> score = new HashMap<Card.Color, Integer>();

        for (Card c : hand) {
            if (c.equals(Card.WELI))
                continue;

            Card.Color color = c.getColor();
            Integer sum = score.get(color);
            int cardValue = c.getValue().intValue();
            score.put(color, sum == null ? cardValue : (sum + cardValue));
        }

        Map.Entry<Card.Color, Integer> best = null, tmp = null;
        Iterator<Map.Entry<Card.Color, Integer>> itr;


        for (itr = pointsPerColor(hand).entrySet().iterator(); itr.hasNext();) {
            tmp = itr.next();

            if (best == null || best.getValue().compareTo(tmp.getValue()) < 0)
                best = tmp;
        }

        return best.getKey();
    }

}