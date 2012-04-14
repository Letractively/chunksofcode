package com.myapp.games.pong;

import java.awt.Graphics;
import javax.swing.JComponent;
import com.myapp.games.framework.awt.AWTGame;

import static com.myapp.games.pong.PongPlayer.LeftPlayer;
import static com.myapp.games.pong.PongPlayer.RightPlayer;


class PongGame extends AWTGame {
    
    
    static interface IPongActor {
        void paint(Graphics g);
        void doGameStuff();
    }
    
    
    private int 
        scoreLimit    =   5,
        xOffset       =   5,
        yOffset       =   5,
        surfaceHeight = 250,
        surfaceWidth  = 250;
    
    boolean gameOver = false;
    private PongPlayer left,right;
    private Ball ball;
    private PongRenderer renderer;
    
    
    public PongGame() {
        int sliderY = 100;
        int sliderHeight = 40;
        int sliderWidth = 10;
        int leftX = 0;
        int rightX = surfaceWidth - sliderWidth;
        
        ball = new Ball(this, 10, surfaceHeight / 2, 0);
        left  = new LeftPlayer(this, sliderY, leftX,  sliderHeight, sliderWidth);
        right = new RightPlayer(this, sliderY, rightX, sliderHeight, sliderWidth);
        
        addKeyListener(left);
        addKeyListener(right);
        renderer = new PongRenderer(this);
    }
    
    
    @Override
    public JComponent getUIComponent() {
        return renderer;
    }

    @Override
    public boolean isExitGame() {
        return gameOver;
    }
    
    
    PongPlayer getLeft() {
        return left;
    }
    PongPlayer getRight() {
        return right;
    }
    Ball getBall() {
        return ball;
    }

    public int getYOffset() {
        return yOffset;
    }
    
    public int getXOffset() {
        return xOffset;
    }
    
    @Override
    protected void executeGameLogic(long gameTime, long gameTimeDeltaMillis) {
        ball.doGameStuff();
        left.doGameStuff();
        right.doGameStuff();
        
        if (left.getScore() >= scoreLimit || right.getScore() >= scoreLimit) {
            gameOver = true;
        }
    }

    @Override
    public int getSurfaceHeight() {
        return surfaceHeight;
    }

    @Override
    public int getSurfaceWidth() {
        return surfaceWidth;
    }

}