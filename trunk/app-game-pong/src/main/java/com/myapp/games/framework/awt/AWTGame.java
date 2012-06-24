package com.myapp.games.framework.awt;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import com.myapp.games.framework.IGameTimeCallback;




public abstract class AWTGame implements Runnable , IGameTimeCallback{

    
    private final class PauseKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_P) {
                if (pause) {
                    resume();
                } else {
                    pause();
                }
            }
        }
    }

    
    private static final int DEFAULT_EXECUTION_DELAY = 50;

    private Thread usainBolt;
    private int cycles = 0;
    private boolean pause = false;
    private int executionDelay = DEFAULT_EXECUTION_DELAY;
    private long gameTime = 0;

    private MultiKeyListener keyListeners = new MultiKeyListener();
    private MultiMouseListener mouseListeners = new MultiMouseListener();

    
    public AWTGame() {
        keyListeners.addListener(new PauseKeyListener());
    }

    
    public abstract int getSurfaceHeight();
    public abstract int getSurfaceWidth();
    public abstract JComponent getUIComponent();
    public abstract boolean isExitGame();
    
    
    /**
     * @param gameTimeNow the game time at the start of this cycle.
     * @param gameTimeDelta the time duration to calculate this cycle.
     */
    protected abstract void executeGameLogic(long gameTimeNow, long gameTimeDelta);
    
    
    public void startGame() {
        usainBolt = new Thread(this);
        usainBolt.start();
    }

    public final void run() {
        for(;; cycles++) {
            while (pause) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            
            long computeStart = System.currentTimeMillis();
            
            executeGameLogic(gameTime, executionDelay);
            getUIComponent().repaint();
            
            if (isExitGame()) {
                logDebug("game over.");
                return;
            }
            
            long computeEnd = System.currentTimeMillis();
            long computeTime = computeStart - computeEnd;
            long sleep = executionDelay - computeTime;
            
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                gameTime += executionDelay;
                
            } else {
                gameTime += computeTime;
                System.err.println(
                    "WARNING: executionDelay < computeTime! " +
                    "delay="+executionDelay+", " +
                    "computeTime="+computeTime
                );
            }
        }
    }
    
    public void pause() {
        pause = true;
        logDebug("game paused.");
    }
    
    public void resume() {
        pause = false;
        logDebug("game resumed.");
    }
    
    public int getCycleNumber() {
        return cycles;
    }
    
    public final long getGameTime() {
        return gameTime;
    }
    
    public final KeyListener getGameKeyListener() {
        return keyListeners;
    }

    public void addKeyListener(KeyListener l) {
        keyListeners.addListener(l);
    }

    public void removeKeyListener(KeyListener l) {
        keyListeners.removeListener(l);
    }
    
    public final MouseListener getGameMouseListener() {
        return mouseListeners;
    }

    public void addMouseListener(MouseListener l) {
        mouseListeners.addListener(l);
    }

    public void removeKeyListener(MouseListener l) {
        mouseListeners.removeListener(l);
    }
    
    public void setExecutionDelay(int delayMillis) {
        this.executionDelay = delayMillis;
    }
    
    public int getExecutionDelay() {
        return executionDelay;
    }

    protected void logDebug(String string) {
        System.out.println(getClass().getSimpleName()+" - cycle: "+cycles+" - "+string);
    }
}