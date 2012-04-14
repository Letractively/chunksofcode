package com.myapp.games.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import com.myapp.games.framework.awt.AWTGameRenderer;

@SuppressWarnings("serial")
class PongRenderer extends AWTGameRenderer {
    
    
    public PongRenderer(PongGame game) {
        super(game);
        Dimension size = new Dimension(game.getSurfaceWidth(), game.getSurfaceHeight());
        super.setPreferredSize(size);
        super.setMinimumSize(size);
    }
    
    @Override
    protected void drawGameObjects(Graphics g) {
        PongGame game = (PongGame) getGame();
        
        // draw white background
        g.setColor(Color.white);
        g.fillRect(0, 0, game.getSurfaceWidth(), game.getSurfaceHeight());
        
        // draw game elements
        game.getBall().paint(g);
        game.getLeft().paint(g);
        game.getRight().paint(g);
        
        if (game.isExitGame()) {
            g.setColor(Color.black);
            g.drawString("Game Over", 100, 125);
        }
    }
}