package com.myapp.games.schnellen.model;

import java.io.Serializable;



public interface IConfig extends Serializable {
    
    int getScoreGoal();
    boolean dealerMaySayBeiMir();
    boolean doubleScoreWhenUntermHund();
    int getMaxCardsChange();
    boolean isDealSixOnChange5();
    boolean isCannotLeaveShellRounds();
    boolean isDoublePointsAfterAllGone();
    boolean isHeartRoundsDouble();
    boolean isPapaHighest();
    boolean isTrumpDeterminedByPunchOffering();
    boolean isTrumpSpellerQuitAllowed();
    boolean isUntermHundEnabled();
    
}