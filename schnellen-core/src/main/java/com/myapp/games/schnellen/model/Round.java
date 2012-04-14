package com.myapp.games.schnellen.model;

import static java.util.Collections.unmodifiableList;

import java.util.Collection;
import java.util.List;

import com.myapp.games.schnellen.frontend.IPlayerFrontend.Event;

/**
 * backs the state of a round in a schnellen game: the played cards, the trump
 * color, the color determinator, the trumpspeller...
 *
 * @author andre
 *
 */
final class Round implements IRound {

    private static final long serialVersionUID = -1668848341041899562L;


    private static final String NL = System.getProperty("line.separator");


    private final Game context;
    private final CardRules cardRules;
    private final List<Card> cardsPlayed, cardsPlayedUnmod;
    
    private String highest = null;
    private boolean initialized = false;
    private String publicWeliOwner = null;


    Round(Game game) {
        this.context = game;
        this.cardRules = new CardRules();
        this.cardsPlayed = new Card.CardList();
        this.cardsPlayedUnmod = unmodifiableList(cardsPlayed);
    }

    final void addPlayCard(Card c, String player) {
        boolean punch = cardRules.punches(c);
        checkCardBeforeAdd(c, player);

        cardsPlayed.add(c);

        if (punch) {
            highest = player;
            context.fireGlobalEvent(Event.SMASH_CARD_PLAYED);
        } else
            context.fireGlobalEvent(Event.CARD_WAS_PLAYED);

        checkAfterAdd(c, player);
        cardRules.setPlayedCards(cardsPlayedUnmod);
    }

    private void checkAfterAdd(Card c, String player) {
        assert cardsPlayedUnmod.contains(c);
        assert ! context.handReadOnly(player).contains(c);

        if (highest == null)
            throw new NullPointerException("no highest player was set! c='" + c + "', played='"+cardsPlayed+"', player='"+player+"'");
    }

    private void checkCardBeforeAdd(Card c, String player) { //TODO tidy up
        String m = "c='"+c+"', players='"+context.players()+"', player='"+player+"', state='"+this+"'";

        Card.Color trump = context.colors().getTrumpSuit();
        assert trump != null : m;
        assert context.players() != null && ! context.players().isEmpty() : m;
        assert ! cardsPlayedUnmod.contains(c) && ! context.handReadOnly(player).contains(c) : m;
        
        if (! context.players().contains(player) || cardsPlayedUnmod.contains(c))
            throw new IllegalStateException(m);

        List<Card> possible = new Card.CardList(context.handReadOnly(player));
        possible.add(c); // this card was already removed from player
        possible = CardRules.calcPossibleCards(cardsPlayedUnmod, possible, trump);
        if ( ! possible.contains(c))
             throw new RuntimeException("Player not allowed to play card! "+m+", possible='"+possible+"'");
    }

    final String firstPlayer() {
        String speller = context.colors().getSpeller();
        if (speller == null)
            return context.players().get(1);
        return speller;
    }

    final int firstPlayersIndex() {
        String firstPlayer = firstPlayer();
        return context.players().indexOf(firstPlayer);
    }

    @Override
    public CardRules getCardRules() {
        return cardRules;
    }

    @Override
    public String getDealer() {
        return context.players().get(0);
    }

    @Override
    public boolean isDealer(String name) {
        assert name != null;
        return getDealer().equals(name);
    }

    @Override
    public int getExchangeCount(String p) {
        return context.backend(p).getExchanged();
    }

    @Override
    public String getHighestPlayer() {
        return highest;
    }
    
    void setHighestPlayer(String player) {
        highest = player;
    }

    @Override
    public String getPublicWeliOwner() {
        return publicWeliOwner;
    }

    @Override
    public int getPunchCount(String p) {
        return context.backend(p).getPunches();
    }

//    @Override
//    public Color getTrumpSuit() {
//        return trump;
//    }

    final void initForNewGame() {
        initialized = true;

        initForNewRound();
        
        System.out.println(NL+"NEW GAME STARTED."+NL+"PLAYERS:"+context.players()+NL);
    }




    final Collection<Card> initForNewRound() {
        if (! initialized)
            throw new IllegalStateException();

//        trump = null;
        highest = null;
        cardRules.setTrumpSuit(null);
        publicWeliOwner = null;
        return removeCards();
    }

    @Override
    public Card lastCard() {
        int size = cardsPlayedUnmod.size();
        assert size > 0;
        return cardsPlayedUnmod.get(size - 1);
    }

    @Override
    public String lastPlayer() {
        int cards = cardsPlayedUnmod.size();
        List<String> players = context.players();

        if (cards == 0) // no card played in this punch yet
            return null;

        // if there is one card, the first player is the last player:
        int index = (firstPlayersIndex() + (cards - 1)) % players.size();

        // first player always has index 1, add number of cards played
        return players.get(index);
    }

    @Override
    public List<Card> playedCards() { 
        return cardsPlayedUnmod;
    }

    final void registerExchangeCount(String p, int count) {
        context.backend(p).setExchanged(count);
    }

    final Collection<Card> removeCards() {
        Collection<Card> drop = new Card.CardList(cardsPlayed);
        cardsPlayed.clear();
        return drop;
    }

    final void setPublicWeliOwner(String publicWeliOwner) {
        if (publicWeliOwner == null)
            throw new NullPointerException();

        this.publicWeliOwner = publicWeliOwner;
        context.fireGlobalEvent(Event.HAS_WELI, publicWeliOwner);
    }

//    final void setTrump(Color trump) {
//        this.trump = trump;
//        highest = null;
//        cardRules.setTrumpSuit(trump);
//
//        if (this.trump == null)
//            return;
//
//        String trumpSpeller = game.colors().getSpeller();
//
//        if (trumpSpeller != null)
//            highest = trumpSpeller;
//        else
//            highest = game.players().get(1);  // next to dealer
//
//        game.fireGlobalEvent(Event.TRUMP_SUIT_DETERMINED);
//    }

    @Override
    public boolean skipsThisRound(String p) {
        return context.backend(p).isSkip();
    }

    @Override
    public String toString() {
        return "CurrentRound [cardsPlayed='" + cardsPlayedUnmod
               + "', highest='" + highest + "']";
    }

    void weliHitAtSplit(String lucky) {
        assert context.handReadOnly(lucky).contains(Card.WELI);
        publicWeliOwner = lucky;
        context.fireGlobalEvent(Event.WELI_HIT_AT_DECKSPLIT);
    }
}
