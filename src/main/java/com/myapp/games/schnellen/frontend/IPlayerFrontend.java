package com.myapp.games.schnellen.frontend;

import java.util.Arrays;
import java.util.List;

import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.IGameContext;


/**
 *
 * @author andre
 *
 */
public interface IPlayerFrontend {


    /**
     *
     * @author andre
     *
     */
    enum Event {
        
        CARD_EXCHANGE_COMPLETED        /* (0)  */  ,
        CARD_WAS_PLAYED                /* (1)  */  ,
        CARDS_DROPPED                  /* (2)  */  ,
        CARDS_RECEIVED                 /* (3)  */  ,
        HAS_WELI                       /* (4)  */  ,
        ROUND_FINISHED                 /* (5)  */  ,
        SCORE_CHANGED                  /* (6)  */  ,
        SCORE_FACTOR_CHANGED           /* (7)  */  ,
        SHELL_ROUND_CANNOT_LEAVE_ROUND /* (8)  */  ,
        SMASH_CARD_PLAYED              /* (9)  */  ,
        TRUMP_SUIT_DETERMINED          /* (10) */  ,
        WELI_HIT_AT_DECKSPLIT          /* (11) */  ,
        UNTERM_HUND                    /* (12) */  ;
            
        private static Event[] VALUES = values();
        
        public static Event getEvent(int id) {
            return VALUES[id];
        }
        
        public static int getId(Event event) {
            assert event != null;
            return Arrays.binarySearch(VALUES, event);
        }
    };

    /**
     * ask the user which cards he wants to exchange.
     * 
     * @param exchangeMax
     * @param deal6
     * @param trump
     * @return a list of cards the user wants to exchange.
     */
    List<Card> askCardsForExchange();

    /**
     * when a user requests 5 cards for exchange he may get six cards, depending
     * on game variant. when he receives 6 cards, he must choose the one he
     * wants to drop.
     */
    Card askDropOneOfSixCards();

    /**
     * when cards are dealt and the trump suit is determined, players are free
     * to leave the game (he is very unhappy with his cards). the player's
     * status will be set to skipped for this round.
     * 
     * @see IPlayerFrontend#isSkipThisRound()
     * @return if this player wishes to skip this round
     */
    boolean askForSkipRound();

    /**
     * ask user where he wants to split the deck. he has to lift between 0 and
     * all cards to lookup weli.
     * 
     * @param deckSize
     *            the number of cards in the deck.
     * @return a number between 1 inclusive and decksize inclusive
     */
    int askSplitDeckAt(int deckSize);

    /**
     * notifies the frontend something happened in the game.
     * 
     * @param id
     *            the event
     */
    void fireGameEvent(Event id);

    /**
     * answers the name of the player assigned to this frontend
     * 
     * @return the name of the player assigned to this frontend
     */
    String getName();

    /**
     * called if and only if the user is untermHund; if the player is unterm
     * hund and he wants to bail out "i am unterm hund" this will return true
     * 
     * @return if the user wants to make public that he is unterm hund. cards
     *         will be redealt.
     */
    boolean wantToBeUntermHund();

    /**
     * when the player splits the deck, he takes a look at the bottom card of
     * the stack he lifted from the deck. this will notify the player which card
     * he lifted. this may be a strategic advantage.
     * 
     * @param card
     */
    void notifyCardLifted(Card card);

    /**
     * when determining the trump suit by making punch offers, every player's
     * visited to make a punch offer
     * 
     * @return the number of punches the player promises to make
     */
    int offerPunch();

    /**
     * ask the player which card to play next. card constraints (defined by
     * {@link Game#round()})
     * 
     * @return
     */
    Card playNextCard(List<Card> possible);


    /**
     * will be called at game start; all queries upon the game state from the
     * frontend can be invoked through this game backend.
     * 
     * @param context
     *            the schnellen game assigned to this frontend
     */
    void setGameContext(IGameContext context);


    /**
     * ask the user which color he wants to announce as trump suit.
     * 
     * @return the trump suit color
     */
    Card.Color spellTrumpSuit();
}