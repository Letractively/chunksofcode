package com.myapp.games.schnellen.model;

import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * internal class that helds the state for a player.
 * 
 * stores the number of punches, the number of cards exchanged and if the
 * player decided to skip the current round.
 *
 * @author andre
 */
final class PlayerBackend {
    
    private final String name;
    private final List<Card> hand, handReadOnly;
    
    private int score = 0;
    private int gamesWon = 0;
    private int exchanged;
    private int punches;
    private boolean skip;

    PlayerBackend(String name) {
        hand = new Card.CardList();
        handReadOnly = unmodifiableList(hand);
        this.name = name;
        resetNewGame();
    }

    void resetNewGame() {
        hand.clear();
        score = 0;
        resetRound();
    }

    void resetRound() {
        punches = 0;
        exchanged = -1;
        skip = false;
    }
    
    @Override
    public String toString() {
        return "PlayerBackend [name=" + name + ", gamesWon=" + gamesWon + ", score=" + score + ", hand=" + handReadOnly + "]";
    }

    String getName() {
        return name;
    }

    List<Card> getHand() {
        return hand;
    }

    List<Card> getHandReadOnly() {
        return handReadOnly;
    }

    int getScore() {
        return score;
    }

    int getGamesWon() {
        return gamesWon;
    }

    int getExchanged() {
        return exchanged;
    }

    int getPunches() {
        return punches;
    }

    boolean isSkip() {
        return skip;
    }

    void setScore(int score) {
        this.score = score;
    }

    void setSkip(boolean skip) {
        this.skip = skip;
    }

    void setExchanged(int count) {
        this.exchanged = count;
    }
    
    void incrementPunchCount() {
        punches++;
    }

    void incrementGamesWon() {
        gamesWon ++;
    }
}