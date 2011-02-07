package com.myapp.games.schnellen.model;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.myapp.games.schnellen.frontend.IPlayerFrontend.Event;


final class Scorings implements IScorings {
    
    private static final String NL = System.getProperty("line.separator");
    
    private final Game context;
    private Collection<String> winners = new HashSet<String>(4);
    private boolean gameWon = false;
    private int scoreFactor = -1;

    
    Scorings(Game context) {
        this.context = context;
    }
    
    @Override
    public int getGamesWon(String player) {
        assert context.players().contains(player);
        return context.backend(player).getGamesWon();
    }

    @Override
    public final int getScore(String p) {
        assert context.players().contains(p);
        return context.backend(p).getScore();
    }

    @Override
    public final boolean isGameFinal() {
        if (gameWon)
            return true;
        
        final int winMin = context.config().getScoreGoal();
        int max = -1;
        
        for (String player : context.players()) {
            PlayerBackend state = context.backend(player);
            int sco = state.getScore();
            
            if (sco > max && sco >= winMin)
                max = sco;
        }

        for (String player : context.players()) {
            PlayerBackend state = context.backend(player);
            int sco = state.getScore();
            
            if (sco == max) {
                state.incrementGamesWon();// maybe two or more have the same score
                winners.add(player);
                gameWon = true;
            }
        }
        
        return false;
    }
    /**
     * updates the score based on the state of the current round.
     */
    void updateScore() {
        final IColors cs = context.colors();
        final IRound round = context.round();
        final List<String> players = context.players();
        final String speller = cs.getSpeller(); // may be null
        final int promise = cs.getSpellersPromise(); // -1 if speller null
        final int longestNameLen = Utilities.longestStringLength(players);
    
        StringBuilder msg = new StringBuilder();
        msg.append("Schnellen.updateScore() factor=").append(scoreFactor);
        msg.append(", trump=").append(cs.getTrumpSuit());
    
        if (promise > 0) {
            assert speller != null;
            msg.append(", promise=").append(promise);
            msg.append(", speller=").append(speller);
        }
    
        msg.append(" ------------------------").append(Game.NL);
    
        List<String> abcPlayers = new ArrayList<String>(players);
        sort(abcPlayers, String.CASE_INSENSITIVE_ORDER);
    
    
        for (String p : abcPlayers) {
            final int punches = round.getPunchCount(p);
            final boolean isSpeller = p == speller; // false if speller is null
            final  boolean skip = round.skipsThisRound(p);
            int points;
            
            if (punches <= 0) {
                if (round.skipsThisRound(p)) {
                    assert ! isSpeller; // trump speller cannot leave the round
                    points = -2;
                } else {                
                    points = -5;        // did not make any punch
                }
            } else if (isSpeller) {
                if (punches < promise) {
                    points = -5;        // did not keep promise
                } else {
                    points = punches; 
                }
            } else {
                points = punches;
            }
    
            incrementScore(p, points);
            appendScoreLog(p,punches,points,isSpeller,longestNameLen,skip,msg);
        }
    
        msg.append(" ------------------------").append(Game.NL);
        System.out.println(msg);
        context.fireGlobalEvent(Event.SCORE_CHANGED);
    }

    @Override
    public final List<Entry<String, Integer>> getRankings() {
        List<Map.Entry<String, Integer>> rankings;
        rankings = new ArrayList<Map.Entry<String, Integer>>();
    
        for (String p : context.players()) {
            // PlayerState state = states.get(p);
            PlayerBackend state = context.backend(p);
            rankings.add(new SimpleEntry<String, Integer>(p, state.getScore()));
        }
    
        // highest first:
        sort(rankings, reverseOrder(Game.CMP));
        return rankings;
    }

    /**
     * appends a line of score results for a specific player to the given
     * stringbuilder.
     * 
     * @param p
     * @param punches
     * @param points
     * @param isSpeller
     * @param longestNameLength
     * @param msg
     */
    private void appendScoreLog(String p,
                        int punches,
                        int points,
                        boolean isSpeller,
                        int longestNameLength,
                        boolean skipped,
                        StringBuilder msg) {
        String delta = Integer.toString(points * scoreFactor);
        String newScore = Integer.toString(context.backend(p).getScore());
        String punchStr = Integer.toString(punches);

        msg.append(p);
        for (int n = longestNameLength-p.length(); n-- > 0; msg.append(' '));

        msg.append(" made ");
        for (int n = 2 - punchStr.length(); n-- > 0; msg.append(' '));
        msg.append(punchStr);

        msg.append(" punches this round(");
        for (int n = 3 - delta.length(); n-- > 0; msg.append(' '));
        msg.append(delta);

        msg.append(" points). Total score: ");
        for (int n = 4 - newScore.length(); n-- > 0; msg.append(' '));
        msg.append(newScore);

        if (points < 0) {
            msg.append("     * ");
            int promise = context.colors().getSpellersPromise();
            
            if (isSpeller && punches < promise)
                msg.append("didn't keep his promise of ").append(promise);
            else if (skipped)
                msg.append("decided to skip this round.");
            else
                msg.append("didn't punch anything");
            msg.append(" *");
        }

        msg.append(Game.NL);
    }

    @Override
    public Collection<String> getWinners() {
        return winners;
    }


    @Override
    public int getScoreFactor() {
        return scoreFactor;
    }

    void initForNewGame() {
        gameWon = false;
        winners.clear();
        initForNewRound();
    }
    
    

    void initForNewRound() {
        scoreFactor = 1;
    }
    

    /**
     * increments the score of the player by the specified number. this number
     * will be multiplied by the round's score factor
     * 
     * @param player
     *            the player
     * @param add
     *            how many steps to increase (or decrase, if negative) the score
     */
    final void incrementScore(String player, int add) {
        assert context.players().contains(player);
        PlayerBackend backend = context.backend(player);
        int score = backend.getScore() + (add * scoreFactor);
        backend.setScore(score);
    }
    

    final void setScoreFactor(int factor) {
        if (scoreFactor == factor)
            return;

        scoreFactor = factor;
        context.fireGlobalEvent(Event.SCORE_FACTOR_CHANGED);
    }

    /**
     * @return a human readable representation of the ranking
     */
    public String getRankingString() {
        StringBuilder score = new StringBuilder();
        int pos = 1;

        for (Map.Entry<String, Integer> e : getRankings()) {
            String name = e.getKey();
            Integer i = e.getValue();
            score.append(pos++);
            score.append(".) ");
            score.append(name);
            for (int j = 20 - name.length(); j-- > 0; score.append(' '));
            score.append(i);
            score.append(NL);
        }

        return score.toString();
    }
}