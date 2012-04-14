package com.myapp.games.towerdefense;

import com.myapp.games.framework.awt.AWTGameLauncher;

public class TowerDefenseMain {

    public static void main(String[] args) {
        TowerDefenseGame.DEBUG_A_STAR = true; // XXX
        
        TowerDefenseGame game = new TowerDefenseGame();
        AWTGameLauncher.launch(game);
    }
}

