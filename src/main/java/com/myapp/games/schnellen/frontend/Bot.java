package com.myapp.games.schnellen.frontend;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.myapp.games.schnellen.model.AbstractPlayerFrontend;
import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.CardRules;
import com.myapp.games.schnellen.model.IColors;
import com.myapp.games.schnellen.model.IConfig;
import com.myapp.games.schnellen.model.IRound;
import com.myapp.games.schnellen.model.Utilities;
import com.myapp.games.schnellen.model.Card.Color;


/**
 *
 * super smart AI-controlled player frontend.
 *
 * @author andre
 *
 */
public class Bot extends AbstractPlayerFrontend {

    private static Random RANDOM = new Random(0L);

    private Comparator<Card> sortByValue = new Utilities.SortByValue();

    public Bot(String name) {
        super(name);
    }


    @Override
    public List<Card> askCardsForExchange() {
        IConfig cfg = game().config();
        int max = cfg.getMaxCardsChange();

        if (max < 1) {
            assert false : max;
            return null;
        }

        Color trump = game().colors().getTrumpSuit();
        List<Card> badCards = new Card.CardList(max);

        for (Iterator<Card> h = hand().iterator(); h.hasNext();) {
            Card c = h.next();

            if (c.isSpecialCard() || c.isPictureCard() || c.getColor() == trump)
                continue; // don't drop these

            badCards.add(c);

            if (badCards.size() >= max)
                break;
        }

        return badCards;
    }

    @Override
    public Card askDropOneOfSixCards() {
        List<Card> hand = hand();
        assert hand.size() == 6 : hand;
        return Collections.min(hand, sortByValue);
    }

    @Override
    public boolean askForSkipRound() {
        return false;
    }


    @Override
    public int askSplitDeckAt(int deckSize) {
        return RANDOM.nextInt(deckSize);
    }

    @Override
    public void fireGameEvent(Event id) {}

    @Override
    public boolean wantToBeUntermHund() {
        return true;
    }

    @Override
    public int offerPunch() {
        IColors cs = game().colors();
        int minimumOffer = cs.getMinimumOfferValue(name);

        if (minimumOffer <= 2)
            return 2;

        return 0;
    }


    @Override
    public Card playNextCard(List<Card> possible) {
        IRound cr = game().round();
        CardRules cardRules = cr.getCardRules();
        List<Card> hand = hand();

        if (possible == null
                || possible.isEmpty()
                || ! hand.containsAll(possible))
            throw new RuntimeException("possible="+possible+", hand="+hand);

        Card drop = null;

        for (Card c : possible)
            if (cardRules.punches(c)) {
                drop = c;
                break;
            }

        if (drop == null)
            drop = possible.get(0);

        return drop;
    }


    @Override
    public Card.Color spellTrumpSuit() {
        Color col = CardRules.suggestColor(hand());
        if (col == null) {
            assert false;
            CardRules.suggestColor(hand());
        }
        return col;
    }
    
    @Override
    public void notifyCardLifted(Card card) {}
}
