package com.myapp.game.foxAndBunny;

import com.myapp.game.foxAndBunny.controller.Controller;

public class FoxAndBunny {
    
    public static void main(String[] args) {
        if (Logger.IS_DEBUG)
            failIfAssertionsDisabled();
        
        Controller game = new Controller();
        game.startGame();
    }
    
    private static void failIfAssertionsDisabled() {
        try {
            assert false;
            throw new RuntimeException("assertions disabled!");
            
        } catch (AssertionError e) {
            // expected
        }
    }
    
}
