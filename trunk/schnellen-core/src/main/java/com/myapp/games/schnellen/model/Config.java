package com.myapp.games.schnellen.model;

/**
 * there are some game variants, this config helds the corresponding attributes.
 *
 * @author andre
 *
 */
public class Config {

    private static Config instance = null;

    static Config getInstance() {
        if (instance == null)
            synchronized (Config.class) {
                if (instance == null)
                    instance = new Config();
            }
        return instance;
    }


    private boolean cannotLeaveShellRounds = true;
    private boolean dealerMaySayBeiMir = true;
    private boolean dealSixCardsOnChangeFive = true;
    private boolean doublePointsAfterAllGone = true;
    private boolean doublePointsAfterUnderTheDog = true;
    private boolean heartRoundsDoublePoints = true;
    private int maxCardsChange = 5;
    private boolean papaIsHighest = true;
    private boolean trumpDeterminedByPunchOffering = true;
    private boolean trumpSpellerQuitAllowed = false;
    private boolean underTheDogEnabled = true;
    private int scoreToWinGame = 15;

    Config() {}

    public int getScoreGoal() {
        return scoreToWinGame;
    }
    
    public boolean dealerMaySayBeiMir() {
        return dealerMaySayBeiMir ;
    }

    /**
     * @return the doublePointsAfterUnderTheDog
     */
    public boolean doublePointsWhenUntermHund() {
        return doublePointsAfterUnderTheDog;
    }

    /**
     * @return the maxCardsChange
     */
    public int getMaxCardsChange() {
        return maxCardsChange;
    }

    /**
     * @return the cannotLeaveShellRounds
     */
    public boolean isCannotLeaveShellRounds() {
        return cannotLeaveShellRounds;
    }

    /**
     * @return the dealSixCardsOnChangeFive
     */
    public boolean isDealSixOnChange5() {
        return dealSixCardsOnChangeFive;
    }

    public boolean isDoublePointsAfterAllGone() {
        return doublePointsAfterAllGone;
    }

    /**
     * @return the heartRoundsDoublePoints
     */
    public boolean isHeartRoundsDouble() {
        return heartRoundsDoublePoints;
    }

    /**
     * @return the papaIsHighest
     */
    public boolean isPapaHighest() {
        return papaIsHighest;
    }

    public boolean isTrumpDeterminedByPunchOffering() {
        return trumpDeterminedByPunchOffering ;
    }

    public boolean isTrumpSpellerQuitAllowed() {
        return trumpSpellerQuitAllowed;
    }

    /**
     * @return the underTheDogEnabled
     */
    public boolean isUntermHundEnabled() {
        return underTheDogEnabled;
    }
}
