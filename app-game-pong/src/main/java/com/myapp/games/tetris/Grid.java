package com.myapp.games.tetris;

import java.awt.Color;
import java.awt.Graphics;

import com.myapp.games.tetris.TetrisGame.ITetrisElement;

public class Grid implements ITetrisElement {
    
    class Tile {
        
        private boolean empty = true;
        private Color color = null;
        
        int row;
        final int col;

        Tile(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        boolean isEmpty() {
            return empty;
        }

        Color getColor() {
            return color;
        }
        
        void render(Graphics g) {
            if ( ! empty) {
                final int tw = game.tileWidth, th = game.tileHeight;
                g.setColor(color);
                g.fillRect(col * tw, row * th, tw, th);
                g.setColor(Color.black);
                g.drawRect(col * tw, row * th, tw, th);
                // g.drawString("r"+col+"c"+col, col * tw, (row+1) * th);
            }
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        @Override
        public String toString() {
            return "Tile[" +(empty?"":"!")+"empty, r=" + row + ", c=" + col + "]";
        }
        
    }
    
    
    private Tile[][] tiles;
    private TetrisGame game;
    
    
    public Grid(TetrisGame game, int rows, int cols) {
        this.game = game;
        this.tiles = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = new Tile(r, c);
                tiles[r][c] = tile;
            }
        }
    }
    
    public void renderElement(Graphics g) {
        for (int r = 0; r < game.rows; r++) {
            for (int c = 0; c < game.columns; c++) {
                Tile tile = tiles[r][c];
                tile.render(g);
            }
        }
    }

    public void doGameStuff() {
        // search for full rows and delete them:
        int rowsDeleted = 0;
        
        for (int rowIndex = 0, tilesLen = tiles.length; rowIndex < tilesLen; rowIndex++) {
            Tile[] row = tiles[rowIndex];
            boolean allFilled = true;
            
            for (Tile t : row) {
                if (t.isEmpty()) {
                    allFilled = false;
                    break;
                }
            }
            
            if (allFilled) {
                deleteRow(rowIndex);
                rowsDeleted++;
                rowIndex--; // jump to same rowindex next step
            }
        }
        
        if (rowsDeleted > 0) {
            game.linesDeleted(rowsDeleted);
        }
    }
    

    private void deleteRow(int deleteIndex) {
        Tile[] deletedRow = tiles[deleteIndex]; // remember row
        
        // shift rows: from last to rowIndex-1 one index lower
        for (int row = deleteIndex; row > 0; row--) {
            tiles[row] = tiles[row - 1];
            for (Tile t : tiles[row]) {
                t.row = row;
            }
        }
        
        // insert deleted row at top:
        for (Tile t : deletedRow) {
            t.empty = true;
            t.row = 0;
        }
        tiles[0] = deletedRow;
    }

    Tile getTile(int row, int col) {
        return tiles[row][col];
    }
}
