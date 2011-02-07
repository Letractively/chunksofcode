package com.myapp.games.schnellen.model;

import static java.util.Collections.rotate;
import static java.util.Collections.shuffle;
import static java.util.Collections.unmodifiableList;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.myapp.games.schnellen.frontend.IPlayerFrontend;
import com.myapp.games.schnellen.frontend.IPlayerFrontend.Event;
import com.myapp.games.schnellen.model.Card.Color;



/**
 * represents a schnellen game.
 * a game runs until one player has reached the top score.
 * 
 * 
 * @author andre
 * 
 */
final class Game implements IGameContext {

    static final Comparator<Entry<String, Integer>> CMP = new Utilities.EntriesByValueComparator();
    static final String NL = System.getProperty("line.separator");
    static final Random RANDOM = new Random(0L);


    private final Round round;
    private final Scorings scorings;
    private final Colors colorSelector;
    private boolean gameStarted = false;
    
    private final List<String> players, playersUnmodifiable;
    private final List<Card> deck;
    private final Map<String, IPlayerFrontend> frontends;
    private final Map<String, PlayerBackend> backends;

    
    /**
     * creates a game with no players initially.
     */
    Game() {
        round = new Round(this);
        scorings = new Scorings(this);
        colorSelector = new Colors(this);
        players = new LinkedList<String>();
        playersUnmodifiable = unmodifiableList(players);
        deck = new Card.CardList(Card.newCardDeck());
        frontends = new HashMap<String, IPlayerFrontend>();
        backends = new HashMap<String,PlayerBackend>();
    }
    
    
    
    
    ///////////////// public interface methods /////////////////////
    
    
    
    
    
    @Override
    public final IRound round() {
        return round;
    }
    
    @Override
    public List<String> players() {
        return playersUnmodifiable;
    }
    
    @Override
    public final IColors colors() {
        return colorSelector;
    }
    
    @Override
    public Config config() {
        return Config.getInstance(); // TODO
    }
    
    @Override
    public final int deckSize() {
        return deck.size();
    }

    @Override
    public IScorings scorings() {
        return scorings;
    }

    @Override
    public final void playGame() {
        initForNewGame();
        gameStarted = true;
    
        do playSingleRound(); while (! scorings.isGameFinal());
    
        gameOver();
    }

    void initForNewGame() {
        for (String p : players) {
            PlayerBackend state = backends.get(p);
            
            if (state == null) {
                state = new PlayerBackend(p);
                backends.put(p, state);
            } else {
                assert state.getName().equals(p);
                state.resetNewGame();
            }
            
            frontend(p).setGameContext(this);
        }
    
        round.initForNewGame();
        scorings.initForNewGame();
    }
    
    
    
    
    
    ///////////////// game business logic ////////////////////
    
    
    
    
    

    /**
     * plays one round: decksplit, deal cards, lookup unterm hund, determine
     * trump suit, handle skippers, exchange bad cards, play cards...
     * 
     * @see the official gamerules :-) //TODO
     * 
     * @see #initRound()
     * @see #splitDeck()
     * @see #dealCards()
     * @see #handleUntermHund()
     * @see #determineTrumpSuit()
     * @see #handleCowards()
     * @see #exchangeCards()
     * @see #playDealedCards()
     * scorings.updateScore()
     * rotate(players, 1)
     */
    void playSingleRound() {
        initRound();
    
        // one player may lift cards and lookup weli:
        splitDeck();
    
        // everyone gets five cards:
        dealCards();
    
        // is anyone under the dog?
        handleUntermHund();
    
        // set the trup suit:
        determineTrumpSuit();
    
        // who is scared and want to go?
        handleCowards();
    
        // players may exchange some cards:
        exchangeCards();
    
        // let's punch 'em:
        playDealedCards();
    
        // count the points of these cards:
        scorings.updateScore();
    
        // set dealer for next round:
        rotate(players, 1);
    }
    
    /**
     * initiates the game state for a new round
     */
    void initRound() {
        for (PlayerBackend ps : backends.values())
            ps.resetRound();
        
        Collection<Card> remainingCards = round.initForNewRound();
        deck.addAll(remainingCards);
        colorSelector.initForNewRound();
        scorings.initForNewRound();
    
    
        int size = players.size();
    
        if (size < 2 || size > 4)
            throw new RuntimeException("too many players: "+players);
    
        // drop player's cards and get a new deck:
        allCardsToDeck(false);
    
        assert deck.size() == 33:"deck="+deck.size()+" "+deck;
    
        shuffle(deck, RANDOM);
    }

    /**
     * at the beginning of each round, the player next to the dealer will choose
     * 1 card from the stack, if it is WELI, the player will take it for this
     * round.<br>
     * the cards above the chosen position (minus the weli with luck) will be
     * put below the cards below the position.
     */
    void splitDeck() {
        boolean foundWeli = false;
    
        if (! deck.contains(Card.WELI)) {
            for (String p : players)
                if (handReadOnly(p).contains(Card.WELI)) {
                    foundWeli = true;
                    round.setPublicWeliOwner(p);
                    break;
                }
    
            if ( ! foundWeli)
                throw new RuntimeException("where is weli?");
        } else
            foundWeli = true;
    
        assert deck.size() == 33 : "deck="+deck.size() +", proto="+ 33;
        foundWeli = false;
        String lucky = players.get(1);
        List<Card> hand = handReadOnly(lucky);
    
        if (hand.size() > 0 && ! ( hand.contains(Card.PAPA) &&
                                      Config.getInstance().isPapaHighest() ) )
            throw new IllegalStateException(""+hand);
    
        IPlayerFrontend plucky = frontend(lucky);
        int choice = plucky.askSplitDeckAt(deck.size());
        List<Card> lifted = new Card.CardList(deck.size());
        ListIterator<Card> i = deck.listIterator(choice + 1);
    
        for (boolean first = true; i.hasPrevious(); first = false) {
            Card card = i.previous();
            i.remove();
    
            if (first && card == Card.WELI) { // jipiie
                foundWeli = true;
                handReadWrite(lucky).add(card);
                round.weliHitAtSplit(lucky);
    
            } else {
                if (first)
                    plucky.notifyCardLifted(card);
                lifted.add(card);
            }
        }
    
        if (foundWeli)
            fireGlobalEvent(Event.WELI_HIT_AT_DECKSPLIT, lucky);
    
        // cards below lifted cards will be appended to the lifted cards:
        deck.addAll(lifted);
        assert deck.size() == (33 - (foundWeli ? 1 : 0)) : "deck:"+deck.size();
    }

    /**
     * deals cards so each player has five cards afterwards.
     */
    void dealCards() {
        // three cards for each player first (two if he luckily took weli,
        // or papa in underdog):
        Iterator<String> iter = players(1).iterator();
        for (; iter.hasNext(); ) {
            String p = iter.next();
            int cardNum = 3 - handReadOnly(p).size(); // lucky
            dealCards(p, cardNum);
        }
    
        // another two cards for everyone:
        for (String p : players(1))
            dealCards(p, 2);
    }

    /**
     * query all players if they are "unterm hund".<br>
     * a player is only asked if he wants to be unterm hund if he really
     * would be.
     * 
     * @see IPlayerFrontend#wantToBeUntermHund()
     * @see Game#isTechnicallyUntermHund(String)
     */
    void handleUntermHund() {
        if (! Config.getInstance().isUntermHundEnabled())
            return;

        boolean untermHund;

        for(;;) {
            untermHund = false;
            
            for (String name : players) {
                List<Card> hand = handReadOnly(name);
                
                if (CardRules.isTechnicallyUntermHund(hand)) {
                    IPlayerFrontend p = frontend(name);
                    
                    if (p.wantToBeUntermHund()) {
                        fireGlobalEvent(Event.UNTERM_HUND);
//                        round.registerUntermHund(name); // TODO
                        untermHund = true;
                        break;
                    }
                }
            }

            if (untermHund) {
                allCardsToDeck(true);
                shuffle(deck);
                dealCards();
                if (Config.getInstance().doublePointsWhenUntermHund())
                    scorings.setScoreFactor(scorings.getScoreFactor() * 2);

                continue;
            }
            
            break;
        }
    }

    /**
     * a color will be announced. cards in the announced colors are higher than
     * the others.<br>
     * player after player, (the player next to the dealer first) may name a
     * number of punches he thinks to make. he may also skip without telling a
     * number.<br>
     * <br>
     * may tell a higher number or skip. the dealer may say an equal high
     * number. (when a player skips, he must not name aPlayer number again this
     * time.)<br>
     * <br>
     * if nobody wants to name a number, the colors will be shuffled and new
     * cards will be dealt, and the score factor will be doubled.<br>
     * the player who names the highest number of punches will announce the
     * color for this round.<br>
     */
    void determineTrumpSuit() {
        Card.Color announcedColor = null;
    
        do {
            announcedColor = colorSelector.determineColor();
    
            if (announcedColor == null) {
                assert config().isTrumpDeterminedByPunchOffering();
                scorings.setScoreFactor(scorings.getScoreFactor() * 2);
                allCardsToDeck(false);
                shuffle(deck);
                dealCards();
                handleUntermHund();
    
            } else {
//                round.setTrump(announcedColor);
                colorSelector.setTrump(announcedColor);
            }
    
        } while (announcedColor == null);
    
        boolean doubleScore = announcedColor == Color.herz
                                  && Config.getInstance().isHeartRoundsDouble();
        if (doubleScore) {
            scorings.setScoreFactor(scorings.getScoreFactor() * 2);
        }
    }

    /**
     * queries all players if they want to stay or skip this round.
     */
    void handleCowards() {
        Config cfg = Config.getInstance();
        if (colorSelector.getTrumpSuit() == Color.schell 
                                           && cfg.isCannotLeaveShellRounds()) {
            fireGlobalEvent(Event.SHELL_ROUND_CANNOT_LEAVE_ROUND);
            return;
        }
    
        int skipped = 0;
        String dealer = colorSelector.getSpeller();
    
        for (IPlayerFrontend p : frontends.values()) {
            String name = p.getName();
            if (name.equals(dealer) && ! cfg.isTrumpSpellerQuitAllowed())
                continue; // trump speller must not quit
    
            if (p.askForSkipRound()) {
                backend(name).setSkip(true);
                dropPlayersCards(name, false);
                skipped++;
            }
        }
    
        if (players.size() - skipped <= 1) { // less than 2 players left
            if (cfg.isDoublePointsAfterAllGone())
                scorings.setScoreFactor(scorings.getScoreFactor() * 2);
            allCardsToDeck(false);
            shuffle(deck);
            dealCards();
            handleUntermHund();
        }
    }

    /**
     * after requesting new cards, this will be called back to deal the new
     * cards to the player.<br>
     * if the count of the new cards is six, the player must return one
     *         card. he has to decide which card in
     *         {@link IPlayerFrontend#askDropOneOfSixCards()}
     */
    void exchangeCards() {
        Config cfg = Config.getInstance();
        int max = cfg.getMaxCardsChange();
    
        // TODO: calculate how much cards left in deck for exchanging!
    
        if (max <= 0) return;
    
        boolean deal6 = (max == 5 && cfg.isDealSixOnChange5());
    
        for (String p : players(1)) {
            final List<Card> markedForXcng = frontend(p).askCardsForExchange();
            if (markedForXcng == null) continue;
    
            int size = markedForXcng.size();
            if (size < 1) continue;
    
            round.registerExchangeCount(p, size);
            cardsToDeck(p, markedForXcng);
            dealCards(p, size);
    
            if (size == 6) {
                assert deal6;
                Card sixth = frontend(p).askDropOneOfSixCards();
                assert sixth != null;
                Object x = handReadOnly(p).remove(sixth);
                assert x != null;
                deck.add(sixth);
            }
        }
    
        fireGlobalEvent(Event.CARD_EXCHANGE_COMPLETED);
    }

    /**
     * let the players  play out their hands by playing five rounds
     */
    void playDealedCards() {
        int first = round.firstPlayersIndex();
    
        do {
            // one card for each player:
            for (String p : players(first)) {
                if (round.skipsThisRound(p))
                    continue;
    
                CardRules rules = round.getCardRules();
                List<Card> possible = rules.calcPossibleCards(handReadOnly(p));
                Card card = frontend(p).playNextCard(possible);
                if (card == null) {
                    throw new NullPointerException();
                }
                handReadWrite(p).remove(card);
                round.addPlayCard(card, p);
            }
    
            String puncher = round.getHighestPlayer();
            backend(puncher).incrementPunchCount();
            assert puncher != null;
            first = players.indexOf(puncher);
            Collection<Card> remainingCards = round.removeCards();
            deck.addAll(remainingCards);
            fireGlobalEvent(Event.ROUND_FINISHED);
    
        } while (! handReadOnly(players.get(0)).isEmpty());
    }

    /**
     * ends the game
     */
    void gameOver() {
        gameStarted = false;
        StringBuilder score = new StringBuilder("Game over!")
                                .append(NL).append(scorings.getRankingString());
        System.out.println(score);
    
        // TODO ASK FOR NEW GAME
    }

    
    
    
    
    
    ////////////// game event stuff ///////////////////////
    
    
    
    
    
    
    /**
     * fires a gaming-event to the assigned playerfrontend
     * 
     * @param player
     *            the player's name
     * @param e
     *            the event
     */
    private void fireEvent(String player, Event e) {
        frontends.get(player).fireGameEvent(e);
    }
    
    /**
     * fires a gaming-event to all playerfrontend
     * 
     * @param e
     *            the event
     */
    final void fireGlobalEvent(Event e) {
        for (String p : players)
            fireEvent(p, e);
    }

    /**
     * fires a gaming-event to all players except to the named players. this may
     * be handy to avoid multiple calls.
     * 
     * @param e
     *            the event
     * @param nots
     *            the players not to notify
     */
    final void fireGlobalEvent(Event e, String... nots) {
        for (String p : players)
            for (String not : nots)
                if (! p.equals(not))
                    fireEvent(p, e);
    }

    
    
    
    
    /////////////////// card management ////////////////////
    
    
    
    
    /**
     * puts the player's cards back to the deck.
     * 
     * @param keepSpecialCards
     *            if the player keps his special cards in their hand
     * @see Card#isSpecialCard()
     * @see Game#allCardsToDeck(boolean)
     */
    private void dropPlayersCards(String name, boolean keepSpecialCards) {
        List<Card> dropping = new Card.CardList(), hand = handReadWrite(name);
        if (hand.isEmpty())
            return;
    
        for (Iterator<Card> i = hand.iterator(); i.hasNext();) {
            Card c = i.next();
    
            if (keepSpecialCards && c.isSpecialCard())
                continue;
    
            dropping.add(c);
        }
    
        cardsToDeck(name, dropping);
    }

    /**
     * puts all cards back to the deck. cards of the players and cards played in
     * the current round.
     * 
     * @param keepSpecialCards
     *            if the players keep their special cards in their hand
     * @see Card#isSpecialCard()
     * @see Game#dropPlayersCards(String, boolean)
     */
    private void allCardsToDeck(boolean keepSpecialCards) {
        for (String p : players)
            dropPlayersCards(p, keepSpecialCards);
        
        deck.addAll(round.removeCards());
    }

    /**
     * removes ALL SPECIFIED cards from the player and puts them to the
     * deck.
     * 
     * @param player the player 
     * @param cards the cards to remove
     */
    private void cardsToDeck(String player, List<Card> cards) {
        if (cards.isEmpty())
            return;
    
        Collection<Card> hand = handReadWrite(player);
        assert hand.containsAll(cards) : hand + " " + cards;
    
        for (Card c : cards) {
            hand.remove(c);
            deck.add(c);
        }
    
        fireEvent(player, Event.CARDS_DROPPED);
    }

    /**
     * answer a writeable view of the player's hand
     * 
     * @param player
     *            the player
     * @return a writeable view of the player's hand
     */
    final List<Card> handReadOnly(String player) {
        return handChecked(player, true);
    }

    /**
     * answer a writeable view of the player's hand
     * 
     * @param player
     *            the player
     * @return a writeable view of the player's hand
     */
    private final List<Card> handReadWrite(String player) {
        return handChecked(player, false);
    }

    /**
     * return the list of cards currently held by the player.
     * 
     * @param name
     *            the player
     * @param readonly
     *            if the returned list is unmodifiable
     * @return the players hand
     */
    private List<Card> handChecked(String name, boolean readonly) {
        assert name != null;
        assert players.contains(name) : name+" "+players;

        PlayerBackend ps = backends.get(name);
        List<Card> cards = readonly ? ps.getHandReadOnly() : ps.getHand();
        return cards;
    }

    /**
     * picks cards from the deck and assign them to a player.
     * 
     * @param player
     *            the player
     * @param cardCount
     *            number of cards to pop from deck
     */
    private void dealCards(String player, int cardCount) {
        // System.out.println("Schnellen.dealCards("+player+", "+cardCount+")");
        assert cardCount > 0 : "player="+player+" cardCount="+cardCount+" hand="+handReadOnly(player);
        List<Card> hand = handReadWrite(player);
    
        for (int j = 0; j < cardCount; j++) {
            Card nextCard = deck.remove(0);
            hand.add(nextCard);
        }

        Utilities.sortHand(hand);
        fireEvent(player, Event.CARDS_RECEIVED);
    }

    /**
     * when not {@link Config#isTrumpDeterminedByPunchOffering()}, this will be
     * called t pick one card from the deck to determine the color. the
     * uncovered card will be put to the bottom of the deck.
     * 
     * @return the card that was at the top of the deck.
     */
    final Card uncoverNextCardForColorChoosing() {
        Card next = deck.remove(0);
        deck.add(next);
        return next;
    }
    
    
    
    
    
    
    //////////////// misc //////////////////
    
    
    
    
    

    /**
     * register a player instance to this game
     * 
     * @param p
     *            the frontend implementation
     * @return whether the players was added in compliance with the naming
     *         constraints
     */
    final boolean addPlayer(IPlayerFrontend p) {
        if (gameStarted) throw new IllegalStateException();
    
        String name = p.getName();
    
        if (players.size() >= 4 || players.contains(name)) {
            System.err.println("WARNING: cold not add player '"+name+"'!");
            return false;
        }
        players.add(name);
        frontends.put(name, p);
        return true;
    }

    /**
     * the player's client object.
     * @param name the players name
     * @return the frontend for the given name
     */
    final IPlayerFrontend frontend(String name) {
        return frontends.get(name);
    }

    /**
     * the server-side state of the player
     * 
     * @param name
     *            the players name
     * @return the backend assigned to this name
     */
    final PlayerBackend backend(String name) {
        return backends.get(name);
    }

    /**
     * iterates over the players starting at the given offset.
     * @param offset the player to start with
     * @return something handy in foreach loops
     */
    private Iterable<String> players(final int offset) {
        return new Utilities.RotatingIterable<String>(playersUnmodifiable, offset);
    }
    
    final Round getRound2() {
        return round;
    }
}
