package com.myapp.games.pong;

import java.awt.Color;
import java.awt.Graphics;

import com.myapp.games.pong.PongGame.IPongActor;



class Ball implements IPongActor {

    private PongGame game;
    
    private final int diameter;
    private int ballX, ballY; // the upper left coords of the ball
    private boolean xIncrease = true;
    private boolean yIncrease = false;
    
    
    public Ball(PongGame game, int diameter) {
        this.diameter = diameter;
        this.game = game;
    }
    
    public Ball(PongGame game, int diameter, int x, int y) {
        this(game, diameter);
        this.ballX = x;
        this.ballY = y;
    }
    
    public void paint(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(ballX, ballY, diameter, diameter);
        
        g.setColor(Color.black);
        g.drawOval(ballX, ballY, diameter, diameter);
    }

    public void doGameStuff() {
        if (isMovingRight()) {
            ballX += game.getXOffset();
            if (isAtRightEdge()) { // ball bounces right
                xIncrease = false;
                game.getLeft().incrementScore();
                logDebug("ball touched right edge. left player has made a point.");
            }
        } else {
            ballX -= game.getXOffset();
            if (isAtLeftEdge()) {  // ball bounces left
                xIncrease = true;
                game.getRight().incrementScore();
                logDebug("ball touched left edge. right player has made a point.");
            }
        }
        
        if (isMovingDown()) {
            ballY += game.getYOffset();
            if (isAtBottomEdge()) { // ball bounces bottom
                yIncrease = false;
            }
        } else {
            ballY -= game.getYOffset();
            if (isAtTopEdge()) {  // ball bounces top
                yIncrease = true;
            }
        }
    }

    
    public boolean isAtRightEdge() {
        int xUpperBound = game.getSurfaceWidth()- diameter;
        return ballX >= xUpperBound;
    }
    
    public boolean isAtLeftEdge() {
        return ballX <= 0;
    }
    
    public boolean isAtTopEdge() {
        return ballY <= 0;
    }
    
    public boolean isAtBottomEdge() {
        int yUpperBound = game.getSurfaceHeight() - diameter;
        return ballY >= yUpperBound;
    }
    
    public int getX() {
        return ballX;
    }

    public int getY() {
        return ballY;
    }

    public int getDiameter() {
        return diameter;
    }

    /**
     * @return true if movint to the right, false if moving to the left
     */
    public boolean isMovingRight() {
        return xIncrease;
    }

    /**
     * @return true if moving downwards, false if moving upwards
     */
    public boolean isMovingDown() {
        return yIncrease;
    }

    public void bounceVertically() {
        xIncrease = ! xIncrease;
    }

    public void bounceHorizontally() {
        yIncrease = ! yIncrease;
    }

    private void logDebug(String string) {
        System.out.println(getClass().getSimpleName()+" - "+string);
    }
}