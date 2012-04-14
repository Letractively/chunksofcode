package com.myapp.games.tetris;

import java.awt.Graphics;

import javax.swing.JComponent;

import com.myapp.games.framework.awt.AWTGame;

class TetrisGame extends AWTGame {

    static interface ITetrisElement {
        void doGameStuff();
        void renderElement(Graphics g);
    }

    
    public final int tileWidth = 35;
    public final int tileHeight = tileWidth;
    public final int rows, columns;
    public final int maxLevel = 10;
    
    private boolean gameOver = false;
    
    private int level = 1;
    private int score = 0;

    private BlockFactory blockFactory;
    private Block currentBlock = null;
    private Grid grid;
    private TetrisRenderer renderer;
    private int lines = 0;
    
    
    public TetrisGame(int rows, int columns) {
        super.setExecutionDelay(30);
        this.rows = rows;
        this.columns = columns;
        this.blockFactory = new BlockFactory(this);
        this.grid = new Grid(this, rows, columns);
        this.renderer = new TetrisRenderer(this);
        insertNewBlock();
    }


    public TetrisGame() {
        this(20, 10);
    }
    
    
    private void insertNewBlock() {
        if (currentBlock != null) {
            removeKeyListener(currentBlock);
        }
        currentBlock = blockFactory.generateRandomBlock();
        int height = currentBlock.calculateHeight();
        currentBlock.setPosition(1 - height, columns / 2 - 2);
        addKeyListener(currentBlock);
        
        if (currentBlock.intersectsFilledTile()) {
            logDebug("game over.");
            gameOver = true;
        }
    }
    
    @Override
    public int getSurfaceHeight() {
        return tileHeight * rows;
    }

    @Override
    public int getSurfaceWidth() {
        return tileWidth * columns;
    }

    @Override
    public JComponent getUIComponent() {
        return renderer;
    }

    @Override
    public boolean isExitGame() {
        return gameOver;
    }
    
    @Override
    protected void executeGameLogic(long gameTime, long gameTimeDeltaMillis) {
        currentBlock.doGameStuff();
        
        if ( ! currentBlock.isInMovement()) {
            insertNewBlock();
        }
        
        grid.doGameStuff(); // check for lines
    }

    Grid getGrid() {
        return grid;
    }
    
    public int getLevel() {
        return level;
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }

    public int getScore() {
        return score;
    }
    
    void linesDeleted(int rowsDeleted) {
        lines  += rowsDeleted;
        score += rowsDeleted * rowsDeleted;
        if (score > level * 20) {
            level++;
        }
    }

    public int getLines() {
        return lines;
    }
}
