package com.myapp.games.schnellen.model;

/**
 * there are some game variants, this config backs the corresponding attributes.
 *
 * @author andre
 *
 */
public class Config implements IConfig {

    private static final long serialVersionUID = 6266348684322480280L;
    
    private boolean cannotLeaveShellRounds = true;
    private boolean dealerMaySayBeiMir = true;
    private boolean dealSixCardsOnChange5 = true;
    private boolean doublePointsAfterAllGone = true;
    private boolean doubleScoreWhenUntermHund = true;
    private boolean heartRoundsDoublePoints = true;
    private boolean papaIsHighest = true;
    private boolean trumpDeterminedByPunchOffering = true;
    private boolean trumpSpellerQuitAllowed = false;
    private boolean untermHundEnabled = true;
    private int maxCardsChange = 5;
    private int scoreGoal = 15;


    Config() {}

    
    @Override
    public int getScoreGoal() {
        return scoreGoal;
    }
    
    @Override
    public boolean dealerMaySayBeiMir() {
        return dealerMaySayBeiMir ;
    }

    @Override
    public boolean doubleScoreWhenUntermHund() {
        return doubleScoreWhenUntermHund;
    }

    @Override
    public int getMaxCardsChange() {
        return maxCardsChange;
    }

    @Override
    public boolean isDealSixOnChange5() {
        return dealSixCardsOnChange5;
    }

    @Override
    public boolean isCannotLeaveShellRounds() {
        return cannotLeaveShellRounds;
    }

    @Override
    public boolean isDoublePointsAfterAllGone() {
        return doublePointsAfterAllGone;
    }

    @Override
    public boolean isHeartRoundsDouble() {
        return heartRoundsDoublePoints;
    }

    @Override
    public boolean isPapaHighest() {
        return papaIsHighest;
    }

    @Override
    public boolean isTrumpDeterminedByPunchOffering() {
        return trumpDeterminedByPunchOffering ;
    }

    @Override
    public boolean isTrumpSpellerQuitAllowed() {
        return trumpSpellerQuitAllowed;
    }

    @Override
    public boolean isUntermHundEnabled() {
        return untermHundEnabled;
    }

    
    
    
    
    
    public void setCannotLeaveShellRounds(boolean cannotLeaveShellRounds) {
        this.cannotLeaveShellRounds = cannotLeaveShellRounds;
    }

    public void setDealerMaySayBeiMir(boolean dealerMaySayBeiMir) {
        this.dealerMaySayBeiMir = dealerMaySayBeiMir;
    }

    public void setDealSixCardsOnChange5(boolean dealSixCardsOnChange5) {
        this.dealSixCardsOnChange5 = dealSixCardsOnChange5;
    }

    public void setDoublePointsAfterAllGone(boolean doublePointsAfterAllGone) {
        this.doublePointsAfterAllGone = doublePointsAfterAllGone;
    }

    public void setDoubleScoreWhenUntermHund(boolean doubleScoreWhenUntermHund) {
        this.doubleScoreWhenUntermHund = doubleScoreWhenUntermHund;
    }

    public void setHeartRoundsDoublePoints(boolean heartRoundsDoublePoints) {
        this.heartRoundsDoublePoints = heartRoundsDoublePoints;
    }

    public void setPapaIsHighest(boolean papaIsHighest) {
        this.papaIsHighest = papaIsHighest;
    }

    public void setTrumpDeterminedByPunchOffering(boolean trumpDeterminedByPunchOffering) {
        this.trumpDeterminedByPunchOffering = trumpDeterminedByPunchOffering;
    }

    public void setTrumpSpellerQuitAllowed(boolean trumpSpellerQuitAllowed) {
        this.trumpSpellerQuitAllowed = trumpSpellerQuitAllowed;
    }

    public void setUntermHundEnabled(boolean untermHundEnabled) {
        this.untermHundEnabled = untermHundEnabled;
    }

    public void setMaxCardsChange(int maxCardsChange) {
        this.maxCardsChange = maxCardsChange;
    }

    public void setScoreGoal(int scoreGoal) {
        this.scoreGoal = scoreGoal;
    }
    
    
}
