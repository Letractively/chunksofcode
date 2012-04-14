package com.myapp.games.framework.awt;

import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AWTGameRenderer extends JPanel {
    
    private final AWTGame game;
    
    protected AWTGameRenderer(AWTGame game) {
        this.game = game;
    }
    
    @Override
    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGameObjects(g);
    }
    
    protected final AWTGame getGame() {
        return game;
    }

    protected abstract void drawGameObjects(Graphics g);
}