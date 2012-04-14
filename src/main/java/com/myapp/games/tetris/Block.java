package com.myapp.games.tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.myapp.games.tetris.BlockIterator.BlockTileVisitor;
import com.myapp.games.tetris.Grid.Tile;
import com.myapp.games.tetris.TetrisGame.ITetrisElement;

public class Block implements ITetrisElement, KeyListener {

    
    final TetrisGame game;
    private Color color = Color.red;
    private int row, col;
    private String name;
    
    
    Block(TetrisGame game, Color color, String name, boolean[][] template) {
        this.game = game;
        this.color = color;
        this.name = name;
        applyTemplate(template);
    }

    
    
    private boolean // store pressed buttons in those flags:
        moveLeftRequested, 
        moveRightRequested, 
        moveDownRequested, 
        rotateRequested;
    
    boolean[][] flags;
    private int height, width;
    private boolean inMovement = true;
    private BlockIterator myCurrentTiles = new BlockIterator();
    
    
    
    public void renderElement(final Graphics g) {
        final int tw = game.tileWidth, th = game.tileHeight;
        
        myCurrentTiles.forEachTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relR, int relC) {
                g.setColor(color);
                g.fillRect(tile.col * tw, tile.row * th, tw, th);
                g.setColor(Color.black);
                g.drawRect(tile.col * tw, tile.row * th, tw, th);
                return true;
            }
        });
    }

    public void doGameStuff() {
        if (moveLeftRequested && ! wouldCollideWhenMoveLeft()) {
            col--; moveLeftRequested = false;
        } 
        if (moveRightRequested && ! wouldCollideWhenMoveRight()) {
            col++; moveRightRequested = false;
        }

        if (rotateRequested && ! wouldCollideWhenRotateRight()) {
            rotateClockwise(); rotateRequested = false;
        }
        
        if (moveDownRequested || wouldMoveDownThisCycle()) {
            moveDownRequested = false;
            if (wouldCollideWhenMoveDown()) {
                fixateBlock();
                return;
            }
            row++;
        }
    }

    private boolean wouldCollideWhenRotateRight() {
        boolean[][] rotated = rotateArray(flags);
        final int len = rotated.length;
        
        for (int relRow = 0; relRow < len; relRow++) {
            boolean[] oneRow = rotated[relRow];
            for (int relCol = 0; relCol < len; relCol++) {
                if (! oneRow[relCol]) {
                    continue;
                }
                int absCol = relCol + col;
                if (absCol < 0 || absCol >= game.columns) { 
                    return true; // deny, bounces left or right
                }
                int absRow = relRow + row;
                if (absRow < 0) { // allow, upwards is "open"
                    continue;
                }
                if (absRow >= game.rows) { // deny, hits bottom
                    return true;
                }
                
            }
            
        }
        return false;
    }

    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    private void applyTemplate(boolean[][] template) {
        height = template.length;
        width = template[0].length;
        
        if (height != width) { // must be a square!
            throw new IllegalArgumentException(Arrays.deepToString(template));
        }
        
        flags = new boolean[height][width];
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                flags[r][c] = template[r][c];
            }
        }
    }
    
    public boolean intersectsFilledTile() {
        final AtomicBoolean intersection = new AtomicBoolean(false);
        
        myCurrentTiles.forEachTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relR, int relC) {
                if (! tile.isEmpty()) {
                    intersection.set(true);
                    return false;
                }
                return true;
            }
        });
        
        return intersection.get();
    }
    
    public int calculateHeight() {
        final int[] colBounds = {100, -1}; // 0=min, 1=max
        
        myCurrentTiles.forEachTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relR, int relC) {
                if (relC > colBounds[1]) {
                    colBounds[1] = relC;
                }
                if (relC < colBounds[0]) {
                    colBounds[0] =relC;
                }
                return true;
            }
        });
        
        int h = colBounds[1] - colBounds[0]; // 0=min, 1=max
        return h;
    }
    
    public boolean isInMovement() {
        return inMovement;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT :  moveLeftRequested = true;  moveRightRequested = false; break;
            case KeyEvent.VK_RIGHT : moveLeftRequested = false; moveRightRequested = true; break;
            case KeyEvent.VK_DOWN :  moveDownRequested = true;  break;
            case KeyEvent.VK_UP :    rotateRequested   = true;  break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT :  moveLeftRequested  = false; break;
            case KeyEvent.VK_RIGHT : moveRightRequested = false; break;
            case KeyEvent.VK_DOWN :  moveDownRequested  = false; break;
            case KeyEvent.VK_UP :    rotateRequested    = false; break;
        }
    }
    
    @Override
    public String toString() {
        return "Block[row=" + row + ", col=" + col + ", name=" + name + "]";
    }

    public void keyTyped(KeyEvent e) { }
    
    private static boolean[][] rotateArray(boolean[][] original) {
        final int M = original.length;
        final int N = original[0].length;
        boolean[][] rotated = new boolean[N][M];
        
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                rotated[c][M-1-r] = original[r][c];
            }
        }
        
        return rotated;
    }
    
    private void rotateClockwise() {
        boolean[][] newState = rotateArray(flags);
        flags = newState;
    }

    private boolean wouldCollideWhenMoveRight() {
        final boolean[] result = {false};
        
        myCurrentTiles.forEachRightTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relR, int relC) {
                if (tile.col >= game.columns - 1) { // collide with right game edge
                    result[0] = true;
                    return false;
                }
                if ( ! game.getGrid().getTile(tile.row, tile.col+1).isEmpty()) {
                    result[0] = true;
                    return false;
                }
                return true;
            }
        });

        return result[0];
    }

    private boolean wouldCollideWhenMoveLeft() {
        final boolean[] result = {false};
        
        myCurrentTiles.forEachLeftTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relR, int relC) {
                if (tile.col <= 0) { // collide with left game edge
                    result[0] = true;
                    return false;
                }
                Tile leftNeighbour = game.getGrid().getTile(tile.row, tile.col-1);
                if ( ! leftNeighbour.isEmpty()) {
                    result[0] = true;
                    return false;
                }
                return true;
            }
        });

        return result[0];
    }

    private boolean wouldMoveDownThisCycle() {
        int frequency = 3 * (game.maxLevel - game.getLevel()) + 1;
        int modulo = game.getCycleNumber() % frequency;
        return 0 == modulo;
    }

    private boolean wouldCollideWhenMoveDown() {
        final boolean[] collision = {false};
        
        myCurrentTiles.forEachBottomTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relR, int relC) {
                if (tile.row >= game.rows - 1) { // collide with bottom of play area
                    collision[0] = true;
                    return false;
                }
                
                Tile tileBelow = game.getGrid().getTile(tile.row + 1, tile.col);
                
                if (! tileBelow.isEmpty()) { // collide with filled tile below
                    collision[0] = true;
                    return false;
                }
                return true;
            }
        });
        
        return collision[0];
    }
    
    private void fixateBlock() {
        myCurrentTiles.forEachTile(this, new BlockTileVisitor() {
            public boolean visit(Tile tile, int relRow, int relCol) {
                tile.setColor(color);
                tile.setEmpty(false);
                return true;
            }
        });
        
        inMovement = false; // TODO: in game abfrage einbauen, und nach ganzen lines checken
    }
}
