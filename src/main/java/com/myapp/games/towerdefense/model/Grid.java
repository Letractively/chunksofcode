package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.myapp.games.towerdefense.model.Tile.Location;

public class Grid implements Iterable<Tile> {
    
    public final int rows, cols, tileDim;
    private final Tile[][] tiles;
    private Tile startTile, targetTile;
    private List<Tile> iteratorBackend;
    
    private BiMap<Tower, Tile> tilesByTower = HashBiMap.create(); 
    
    
    public Grid(int rows, int cols, int tileDim) {
        if (rows < 0 || cols < 0 || tileDim % 2 != 0) {
            throw new RuntimeException("rows=" + rows + ", cols=" + cols
                                       + ", tileDim=" + tileDim);
        }
        this.rows = rows;
        this.cols = cols;
        this.tileDim = tileDim;
        this.tiles = new Tile[rows][cols];
        initTiles();
        iteratorBackend = Collections.unmodifiableList(initIteratorBackend());
    }
    

    private void initTiles() {
        for (int r = 0, arrayLen = tiles.length; r < arrayLen; r++) {
            Tile[] row = tiles[r];
            
            for (int c = 0, rowLen = row.length; c < rowLen; c++) {
                Point absolutePos = new Point(c * tileDim, r * tileDim);
                Tile tile = new Tile(r, c, absolutePos, tileDim);
                tiles[r][c] = tile;
            }
        }
    }
    
    public List<Tile> initIteratorBackend() {
        List<Tile> result = new ArrayList<Tile>(rows*cols);
        
        for (int i = 0, tilesLen = tiles.length; i < tilesLen; i++) {
            Tile[] row = tiles[i];
            
            for (int j = 0, rowLen = row.length; j < rowLen; j++) {
                Tile t = row[j];
                result.add(t);
            }
        }

        return result;
    }
    
    
    public Tile getTileAt(int r, int c) {
        return tiles[r][c];
    }
    
    public Tile getTileAt(Point absolutePosition) {
        int row = absolutePosition.y / tileDim;
        int col = absolutePosition.x / tileDim;
        
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return getTileAt(row, col);
    }
    
    Tile getNeighbour(Tile t, Location direction) {
        switch (direction) {
            case TOP_LEFT    : return getTileAt(t.row-1, t.col-1);
            case TOP         : return getTileAt(t.row-1, t.col  );
            case TOP_RIGHT   : return getTileAt(t.row-1, t.col+1);
            case RIGHT       : return getTileAt(t.row  , t.col+1);
            case LOWER_RIGHT : return getTileAt(t.row+1, t.col+1);
            case LOWER       : return getTileAt(t.row+1, t.col  );
            case LOWER_LEFT  : return getTileAt(t.row+1, t.col-1);
            case LEFT        : return getTileAt(t.row  , t.col-1);
        }
        throw new RuntimeException("tile="+t+", direction="+direction.name());
    }

    public Tile getStartTile() {
        return startTile;
    }

    public Tile getTargetTile() {
        return targetTile;
    }
    
    @Override
    public String toString() {
        return "Grid [rows="+rows+", cols="+cols+", tileDim="+tileDim+"]";
    }

    public ListIterator<Tile> iterator() {
        return iteratorBackend.listIterator();
    }
    
    void setStartTile(Tile startTile) {
        this.startTile = startTile;
    }
    
    void setTargetTile(Tile targetTile) {
        this.targetTile = targetTile;
    }
    
    public Collection<Tower> getTowers() {
        return tilesByTower.keySet();
    }

    public void setTower(int row, int col, Tower tower) {
        Tile tileAt = getTileAt(row, col);
        
        if (tower == null) {
            tileAt.setHasTower(false);
            tilesByTower.inverse().remove(tileAt);
            
        } else {
            assert ! tilesByTower.containsKey(tower);
            assert ! tileAt.hasTower();
            tileAt.setHasTower(true);
            tilesByTower.put(tower, tileAt);
            Point pos = tileAt.getPos(Location.CENTER);
            tower.getAbsolutePosition().setLocation(pos);
        }
    }
    
    boolean hasTower(Tile tile) {
        Tower tower = tilesByTower.inverse().get(tile);
        return tower != null;
    }
}
