package com.myapp.games.tetris;

import java.awt.Color;
import java.awt.Graphics;

import com.myapp.games.framework.awt.AWTGame;
import com.myapp.games.framework.awt.AWTGameRenderer;

@SuppressWarnings("serial")
public class TetrisRenderer extends AWTGameRenderer {

    protected TetrisRenderer(AWTGame game) {
        super(game);
    }

    
    @Override
    protected void drawGameObjects(Graphics g) {
        TetrisGame game = (TetrisGame) getGame();

        // draw white background
        g.setColor(Color.white);
        g.fillRect(0, 0, game.getSurfaceWidth(), game.getSurfaceHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, game.getSurfaceWidth(), game.getSurfaceHeight());

        Grid grid = game.getGrid();
        grid.renderElement(g);
        
        Block block = game.getCurrentBlock();
        block.renderElement(g);

        g.setColor(Color.black); 
        g.drawString("score:"+game.getScore()+" lines:"+game.getLines()+" level:"+game.getLevel(), 12, 12);
        
        if (game.isExitGame()) {
            g.setColor(Color.black);
            g.drawString("Game Over", 100, 125);
        }
    }

}
