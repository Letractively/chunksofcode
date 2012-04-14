package com.myapp.games.tetris;

import com.myapp.games.framework.awt.AWTGameLauncher;

public class TetrisMain {
    
    public static void main(String[] args) {
        TetrisGame game = new TetrisGame();
        AWTGameLauncher.launch(game);
    }
    
}
