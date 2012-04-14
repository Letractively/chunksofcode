package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;


public class Tile {
    
    public enum Location {
        CENTER      ,
        TOP_LEFT    ,
        TOP         ,
        TOP_RIGHT   ,
        RIGHT       ,
        LOWER_RIGHT ,
        LOWER       ,
        LOWER_LEFT  ,
        LEFT        ;

        public static boolean isDiagonal(Location l) {
            switch (l) {
                case TOP_LEFT:
                case TOP_RIGHT:
                case LOWER_RIGHT:
                case LOWER_LEFT:
                    return true;
            }
            return false;
        }
    }
    
    public final int row, col;
    private final Point absolutePos;
    private Map<Location, Point> locations = new HashMap<Location, Point>();
    private boolean hasTower = false;
    private final int dim;
    private boolean isWalkable, isBuildable;
    
    
    public Tile(int row, int col, Point absolutePosition, int tileDim) {
        this.row = row;
        this.col = col;
        this.dim = tileDim;
        this.absolutePos = absolutePosition;
        isBuildable = true;
        isWalkable = true;
        initLocations();
    }
    
    private void initLocations() {
        Point a = absolutePos;
        int half = dim / 2;
        
        locations.put(Location.CENTER     , new Point(a.x + half, a.y + half)); 
        locations.put(Location.TOP_LEFT   , new Point(a.x       , a.y       )); 
        locations.put(Location.TOP        , new Point(a.x + half, a.y       )); 
        locations.put(Location.TOP_RIGHT  , new Point(a.x + dim , a.y       )); 
        locations.put(Location.RIGHT      , new Point(a.x + dim , a.y + half)); 
        locations.put(Location.LOWER_RIGHT, new Point(a.x + dim , a.y + dim )); 
        locations.put(Location.LOWER      , new Point(a.x + half, a.y + dim )); 
        locations.put(Location.LOWER_LEFT , new Point(a.x       , a.y + dim )); 
        locations.put(Location.LEFT       , new Point(a.x       , a.y + half)); 
    }
    
    public boolean isMountable(Tower t) {
        assert t != null;
        return hasTower();
    }
    
    public boolean hasTower() {
        return hasTower;
    }
    
    public Point absPos() {
        return absolutePos;
    }
    
    public Point getPos(Location location) {
        return locations.get(location);
    }
    
    public void setHasTower(boolean hasTower) {
        this.hasTower = hasTower;
    }

    public boolean isWalkable() {
        if (hasTower())
            return false;
        return isWalkable;
    }

    void setWalkable(boolean isWalkable) {
        this.isWalkable = isWalkable;
    }

    public boolean isBuildable() {
        return isBuildable;
    }

    void setBuildable(boolean isBuildable) {
        this.isBuildable = isBuildable;
    }
    

    @Override
    public String toString() {
        return "Tile [row=" + row + ", " +
        		     "col=" + col + ", " +
//		     		 "absolutePos=" + absolutePos + ", " +
     				 "tower=" + hasTower + "]";
    }
}
