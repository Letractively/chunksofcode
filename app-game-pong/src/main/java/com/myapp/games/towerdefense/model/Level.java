package com.myapp.games.towerdefense.model;

public class Level extends DelayedInserter<Wave> {

    private int startRow, startCol, targetRow, targetCol;
    
    public Level(int startRow,
                 int startCol,
                 int targetRow,
                 int targetCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }
    
    public void setUpModel(Grid grid) {
        Tile startTile = grid.getTileAt(startRow, startCol);
        Tile targetTile = grid.getTileAt(targetRow, targetCol);
        grid.setStartTile(startTile);
        grid.setTargetTile(targetTile);
    }
    
    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }
}
