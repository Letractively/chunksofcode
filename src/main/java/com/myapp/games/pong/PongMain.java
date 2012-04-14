package com.myapp.games.pong;

import com.myapp.games.framework.awt.AWTGameLauncher;

public class PongMain {
    
    public static void main(String[] args) {
        PongGame game = new PongGame();
        AWTGameLauncher.launch(game);
    }
    
}
