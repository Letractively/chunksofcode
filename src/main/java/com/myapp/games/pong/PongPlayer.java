package com.myapp.games.pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.myapp.games.pong.PongGame.IPongActor;


abstract class PongPlayer implements IPongActor, KeyListener {
    
    static class LeftPlayer extends PongPlayer {
        LeftPlayer(PongGame game, int y, int x, int h, int w) {
            super(game, y, x, h, w);
        }
        
        boolean ballAtBouncingXCoords() {
            //     (TODO: implement bouncing with upper and lower edge of sliders too)
            Ball ball = game.getBall();
            
            if (ball.isMovingRight()) { // none of my business
                return false; 
            }
            
            boolean ballMayBounce = ball.getX() == sliderWidth;
            return ballMayBounce;
        }

        public void keyPressed(KeyEvent arg0) {
            switch (arg0.getKeyCode()) {
                case KeyEvent.VK_W: upPressed = true; downPressed = false; break;
                case KeyEvent.VK_S: downPressed = true; upPressed = false; break;
            }
        }

        public void keyReleased(KeyEvent arg0) {
            switch (arg0.getKeyCode()) {
                case KeyEvent.VK_W: upPressed   = false; break;
                case KeyEvent.VK_S: downPressed = false; break;
            }
        }
    }
    
    static class RightPlayer extends PongPlayer {
        RightPlayer(PongGame game, int y, int x, int h, int w) {
            super(game, y, x, h, w);
        }
        
        boolean ballAtBouncingXCoords() {
            //     (TODO: implement bouncing with upper and lower edge of sliders too)
            Ball ball = game.getBall();
            
            if (! ball.isMovingRight()) {// none of my business
                return false; 
            }
            
            int rightBallX = ball.getX() + ball.getDiameter();
            int leftSliderX = game.getSurfaceWidth() - sliderWidth;
            boolean ballMayBounce = rightBallX == leftSliderX;
            return ballMayBounce;
        }

        public void keyPressed(KeyEvent arg0) {
            switch (arg0.getKeyCode()) {
                case KeyEvent.VK_UP: upPressed = true;   downPressed = false; break;
                case KeyEvent.VK_DOWN: downPressed = true; upPressed = false; break;
            }
        }

        public void keyReleased(KeyEvent arg0) {
            switch (arg0.getKeyCode()) {
                case KeyEvent.VK_UP:   upPressed   = false; break;
                case KeyEvent.VK_DOWN: downPressed = false; break;
            }
        }
    }
    
    

    protected PongGame game;
    protected int y, x; // the upper left coords of the slider

    protected int score = 0;
    protected int sliderHeight;
    protected int sliderWidth;
    
    protected boolean upPressed   = false;
    protected boolean downPressed = false;

    PongPlayer(PongGame game, int sliderY, int sliderX, int sliderHeight, int sliderWidth) {
        this.game = game;
        this.y = sliderY;
        this.x = sliderX;
        this.sliderHeight = sliderHeight;
        this.sliderWidth = sliderWidth;
    }

    abstract boolean ballAtBouncingXCoords();
    
    public void setSliderY(int sliderY) {
        this.y = sliderY;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getScore() {
        return score;
    }
    
    public boolean isAtTopEdge() {
        return y <= 0;
    }
    
    public boolean isAtBottomEdge() {
        int yUpperBound = game.getSurfaceHeight() - sliderHeight;
        return y >= yUpperBound;
    }
    
    public void incrementScore() {
        score++;
    }

    public int getSliderVert() {
        return sliderHeight;
    }
    
    public void doGameStuff() {
        ///// move
        
        if (upPressed && ! isAtTopEdge()) {
            y -= game.getYOffset();
        }
        if (downPressed && ! isAtBottomEdge()) {
            y += game.getYOffset();
        }

        ////// calculate bouncing with sliders: 
        
        if (ballAtBouncingXCoords() && ballAtBouncingYCoords()) {
            game.getBall().bounceVertically();
            logDebug("ball bounced with "+(x==0?"left":"right")+" slider.");
        }
    }
    
    private boolean ballAtBouncingYCoords() {
        Ball ball = game.getBall();
        int ballRadius = ball.getDiameter() / 2;
        int ballCenterY = ball.getY() + ballRadius;
        
        if (ballCenterY >= y && ballCenterY <= (y + sliderHeight)) {
            return true;
        }
        return false;
    }

    public void paint(Graphics g) {
        Color color = null;
        int scoreX, scoreY = 10;
        String title = null;
        
        if (x == 0) {
            title = "left player";
            color = Color.blue;
            scoreX = 5;
        } else  {
            title = "right player";
            color = Color.red;
            scoreX = 140;
        }
        
        g.setColor(color);
        g.drawString(title +": " + score, scoreX, scoreY);
        g.fillRect(x, y, sliderWidth, sliderHeight);
        
        g.setColor(Color.black);
        g.drawRect(x, y, sliderWidth, sliderHeight);
    }

    public void keyTyped(KeyEvent arg0) { }
    
    void logDebug(String string) {
        System.out.println(getClass().getSimpleName()+" - "+string);
    }
}