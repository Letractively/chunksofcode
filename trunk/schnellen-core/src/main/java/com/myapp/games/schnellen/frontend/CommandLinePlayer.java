package com.myapp.games.schnellen.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.myapp.games.schnellen.model.Card;
import com.myapp.games.schnellen.model.Card.Color;
import com.myapp.games.schnellen.model.CardRules;
import com.myapp.games.schnellen.model.Config;
import com.myapp.games.schnellen.model.IColors;
import com.myapp.games.schnellen.model.IRound;

public class CommandLinePlayer extends HumanPlayerFrontend {


    private static final String NL = System.getProperty("line.separator");


    public CommandLinePlayer(String name) {
        super(name);
    }


    @Override
    public List<Card> askCardsForExchange() {
        Config cfg = game().config();
        int max = cfg.getMaxCardsChange();
        Color trump = game().colors().getTrumpSuit();
        boolean deal6 = (max == 5 && cfg.isDealSixOnChange5());

        IO.println("------------------------");
        assert hand().size() == 5 : hand();
        IO.println("You may now exchange up to "+max+" cards. " +NL+
        		   "Trump suit='"+trump+"'");

        if (deal6 && max == 5)
            IO.println("(if exchanging all cards, you will get 6 keep 5 cards)");

        List<Card> droppedCards = selectCards(0, max);
        return droppedCards;
    }

    @Override
    public Card askDropOneOfSixCards() {
        List<Card> hand = hand();
        assert hand.size() == 6 : hand;
        IO.println("------------------------"+NL+
                   "You received 6 cards, and you have to drop one of them.");
        Card drop = selectCard();
        return drop;
    }

    @Override
    public boolean askForSkipRound() {
        IO.println("------------------------");
        IO.println("Cards were dealt. Do you wish to stay or SKIP THIS ROUND?");
        printCards("Your cards: ", false);
        IO.println("Enter 'skip' or 's' to suppress this round, or " +
        		   "nothing to continue this round:");
        String line = IO.readln().trim();
        return line.matches("(?i)s(kip)?");
    }

    @Override
    public int askSplitDeckAt(int deckSize) {
        IO.println("------------------------");
        return IO.readInt("You can now lookup the weli card in the deck. " +NL+
                          "How many cards do you want to lift?",
                          1,
                          deckSize,
                          1 + new Random().nextInt(deckSize)
        );
    }

    @Override
    public void fireGameEvent(Event id) {
        IRound cr = game().round();
        IColors cs = game().colors();
        switch (id) {
        case SCORE_FACTOR_CHANGED : {
            IO.println("The score factor changed: points in this round will be"+
            	     " multiplied by "+game().scorings().getScoreFactor()+" !");
            break;

        } case TRUMP_SUIT_DETERMINED : {
            String ts = cs.getSpeller();
            String whom = (ts == null ? "." : " by "+ts+".");
            IO.println("The trump suit was set to "+cs.getTrumpSuit()+whom);
            break;

        } case HAS_WELI : {
            String owner = cr.getPublicWeliOwner();
            assert owner != null;
            IO.println("This player still has the weli card: "+owner);
            break;

        } case WELI_HIT_AT_DECKSPLIT : {
            String owner = cr.getPublicWeliOwner();
            assert owner != null;
            IO.println((owner.equals(name) ?"Cool. You":owner)+" hit the weli card!");
            break;

        }
        case SMASH_CARD_PLAYED : // fall through
        case CARD_WAS_PLAYED : {
            String msg = cr.lastPlayer()+" played card: "+cr.lastCard();
            if (id == Event.SMASH_CARD_PLAYED)
                msg += " (Currently the higest card)";
            IO.println(msg);
            break;
        }
        case CARD_EXCHANGE_COMPLETED : {
            for (String p : game().players())
                if (p != name)
                    IO.println(p+" exchanged "+cr.getExchangeCount(p)+" cards.");
            break;
        }
        case ROUND_FINISHED : {
            IO.println("This punch was hit by "+cr.getHighestPlayer()+NL);
            break;
        }
        case SCORE_CHANGED : {
            List<Map.Entry<String, Integer>> rankings = game().scorings().getRankings();
            IO.println("Current score ranking:");
            int i = 1;

            for (Iterator<Entry<String, Integer>> itr = rankings.iterator(); itr.hasNext(); ) {
                Entry<String, Integer> next = itr.next();
                IO.println(i+++" .) "+next.getKey()+"    "+next.getValue());
            }
            break;
        }
        case CARDS_RECEIVED : {
            IO.println("You received new card(s). Your hand: "+hand());
            break;
        }
        case CARDS_DROPPED : {
            IO.println("You dropped card(s). Your hand: "+hand());
            break;
        }
        case UNTERM_HUND : { // TODO
            IO.println(NL+"Unterm hund! Cards will be redealt!");
            break;
        }
        case SHELL_ROUND_CANNOT_LEAVE_ROUND : { // TODO
            throw new UnsupportedOperationException("not yet implemented");
        }
        default: throw new RuntimeException(id+"");
        }
    }

    @Override
    public boolean wantToBeUntermHund() {
        IO.println("------------------------"+NL+
                   "You are unterm hund! Do you want to tell the others "+
                   "and REDEAL CARDS?" +NL);
        printCards("Your cards: ", false);
        IO.println("Enter 'redeal' or 'r' to skip this round:");
        String line = IO.readln().trim();
        return line.matches("(?i)r(edeal)?");
    }

    @Override
    public void notifyCardLifted(Card card) {
        IO.println("This card was visible during deck splitting: "+card);
    }

    @Override
    public int offerPunch() {
        IO.println("------------------------");
        IColors cs = game().colors();
        int promise = cs.getSpellersPromise();
        int minimumOffer = cs.getMinimumOfferValue(name);

        IO.println(
            "You are asked for YOUR PUNCH COUNT. If you are the one "+NL+
            "with the highest promise, you will determine the trump suit. "+NL+
            "If you won't make that much punches in the following round, "+NL+
            "you will gain 5 points up!"+NL+NL+
            "CURRENT OFFER: "+
            (promise <= 0 ? "none" : promise+" (by "+cs.getSpeller()+")")
        );

        printCards("Your cards: ", false);

        return IO.readInt(
            "Enter your offer (or a number < "+minimumOffer+" to skip)",
            minimumOffer,
            5,
            minimumOffer
        );
    }

    @Override
    public Card playNextCard(List<Card> possible) {
        IRound cr = game().round();
        IO.println("------------------------"+NL+
                   "current round's played cards:");
        IO.printCards(cr.playedCards(), true);
        IO.println("Now select your card.");
        Card card = IO.selectCard(possible);
        return card;
    }

    private void printCards(String msg, boolean numbered) {
        IO.printCards(msg, hand(), numbered);
    }

    private Card selectCard() {
        return IO.selectCard(hand());
    }


    private List<Card> selectCards(int min, int max) {
        return IO.selectCards(hand(), min, max);
    }

    @Override
    public Color spellTrumpSuit() {
        IO.println("------------------------"+NL+
                   "You can now SET the TRUMP color for this round!");
        printCards("Your cards: ", false);
        return IO.selectColor(hand());
    }
}












final class IO {

    private static final String NL = System.getProperty("line.separator");
    private static BufferedReader STD_IN_READER;

    static {
        STD_IN_READER = new BufferedReader(new InputStreamReader(System.in));
    }

    static final void printCards(List<Card> cards, boolean numbered) {
        printCards(null, cards, numbered);
    }

    static final void printCards(String msg, List<Card> cards, boolean numbered) {
        StringBuilder bui = new StringBuilder(msg == null ? "" : msg);

        for (int i = 0, s = cards.size(); i < s;)
            if (numbered) {
                bui.append(i).append(": ").append(cards.get(i++));

                if (i < s)
                    bui.append(NL);
            } else
                bui.append(cards.get(i++)).append(" ");

        println(bui.toString());
    }

    static final void println(String msg) {
        System.out.println(msg);
    }

    static final int readInt(String msg, int min, int max, int defoult) {
        boolean withDefault = defoult <= max && defoult >= min;

        println(
             msg + NL + "(between "+min+" and "+max+
             (withDefault ? ", or hit RETURN to select "+defoult+")" : "") +
             ")"
        );

        for (;;) {
            String line = readln().trim();
            int num = -1;

            try {
                num = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                if (withDefault && line.trim().isEmpty())
                    return defoult;
                println(e.getMessage());
                continue;
            }

            if (num < min || num > max)
                continue;

            return num;
        }
    }

    static final String readln() {
        try {
            return STD_IN_READER.readLine();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static final Card selectCard(List<Card> cards) {
        IO.printCards(cards, true);

        assert cards != null && ! cards.isEmpty();
        boolean onlyOne = cards.size() == 1;

        if (onlyOne)
            IO.println("There is only one card left. Hit RETURN to choose it.");

        for (;;) {
            if (! onlyOne) IO.println("Enter a number to select a card:");

            String line = IO.readln().trim();

            if (onlyOne && line.isEmpty())
                return cards.get(0);

            int n;
            try {
                n = Integer.parseInt(line);

            } catch (NumberFormatException e) {
                IO.println("ERROR: not a number: '"+line+"'");
                continue;
            }

            if (n < 0 || n >= cards.size()) {
                IO.println("ERROR: index out of bounds: n (" + n + ") " +
                           "must be < 0 and >="+cards.size()+"!");
                continue;
            }

            return cards.get(n);
        }
    }

    static final List<Card> selectCards(List<Card> cards, int min, int max) {
        IO.printCards(cards, true);

        for (;;) {
            IO.println("Enter comma-separated numbers to select " +
                               "between "+min+" and "+max+" cards:");
            if (max >= cards.size())
                IO.println("(enter 'all' or 'a' to select all cards)");
            if (min <= 0)
                IO.println("(hit RETURN to select zero cards!)");

            String line = IO.readln().trim();

            if (line.isEmpty() && min <= 0)
                return Collections.emptyList();

            if (line.matches("(?i)a(ll)?"))
                return new Card.CardList(cards);

            if (! line.matches("^(\\d(?:,|$)){"+min+","+max+"}")) {
                System.err.println("not a comma separated number list with "+
                                   min+" < elements < "+max+" : "+line);
                continue;
            }

            String[] digits = line.split(",");
            if (digits.length < min || digits.length > max) {
                System.err.println("you have to select between "+
                                   min+" and "+max+" cards: '"+digits+"'!");
                continue;
            }

            List<Card> selectedCards = new Card.CardList();

            for (String num : digits) {
                int n = Integer.parseInt(num);
                if (n < 0 || n > cards.size()) {
                    System.err.println("index out of bounds: " + n + " " +
                                       "not between 1 and "+cards.size());
                    continue;
                }

                selectedCards.add(cards.get(n));
            }

            return selectedCards;
        }
    }

    static final Color selectColor(List<Card> hand) {
        StringBuilder bui = new StringBuilder();
        Card.Color[] colors = Card.Color.values();
        Color suggestion = CardRules.suggestColor(hand);
        int suggestedIndex = -1;

        for (int i = 0, s = colors.length; i < s; i++) {
            Card.Color c = colors[i];

            bui.append(i);
            bui.append(": ");
            bui.append(c);

            if (c == suggestion) {
                suggestedIndex = i;
                bui.append("      * suggested *");
            }

            if (i+1 < s)
                bui.append(NL);
        }

        println(bui.toString());
        int index = readInt("Enter a number to select a color:" ,
                            0, colors.length-1, suggestedIndex);
        return colors[index];
    }
}
