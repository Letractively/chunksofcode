package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myapp.games.towerdefense.model.Tile.Location;


public class AStar {
    
    private class TileCmp implements Comparator<Tile> {

        // heuristically compare tiles based the sum of the straight 
        // distance to the target field and the walked distance from the start
        
        public int compare(Tile o1, Tile o2) {
            Point targetPos = targetTile.absPos();
            
            double targetStraight1 = targetPos.distance(o1.absPos());
            double targetStraight2 = targetPos.distance(o2.absPos());
            
            double alreadyWalked1 = distancesFromStart.get(o1);
            double alreadyWalked2 = distancesFromStart.get(o2);
            
            double distance1 = targetStraight1 + alreadyWalked1;
            double distance2 = targetStraight2 + alreadyWalked2;
            
            if (Math.abs(distance1 - distance2) < 0.001d) {
                return 0;
            }
            if (distance1 < distance2) {
                return -1;
            }
            return 1;
        }
    }

    private TileCmp heuristic = new TileCmp();
    
    private final GameModel model;
    private final Grid modelGrid;
    
    private Tile startTile;
    private Tile targetTile;
    
    private boolean ignoreBarriers;
    
    final Map<Tile, Double> distancesFromStart = new HashMap<Tile, Double>();
    final List<Tile> possibleTiles = new ArrayList<Tile>();
    final Map<Tile, Tile> previousTiles = new HashMap<Tile, Tile>();
    

    public AStar(GameModel model) {
        this.model = model;
        this.modelGrid = this.model.getGrid();
    }

    public List<Tile> calculatePath(Tile fromTile, Tile toTile, boolean canFly) {
        synchronized (this) {
            this.startTile = fromTile;
            this.ignoreBarriers = canFly;
            this.targetTile = toTile;
            return calc();
        }
    }

    public List<Point> calculatePointPath(Point absPos, boolean canFly) {
        synchronized (this) {
            final Tile tile = modelGrid.getTileAt(absPos);
            final Tile goalTile = modelGrid.getTargetTile();
            
            List<Point> points = calculatePointPath(tile, goalTile, canFly);
            points.set(0, absPos);
            return points;
        }
    }
    
    public List<Point> calculatePointPath(Enemy enemy) {
        synchronized (this) {
            final Point enemyPos = enemy.getAbsPos();
            final Tile enemyTile = modelGrid.getTileAt(enemyPos);
            final Tile goalTile = modelGrid.getTargetTile();
            assert enemy != null;
            assert enemyTile != null;
            assert goalTile != null;
    
            List<Point> points = calculatePointPath(enemyTile, goalTile, enemy.canFly());
            if (points == null) {
                return null;
            }
            points.remove(0);
            return points;
        }
    }

    private List<Point> calculatePointPath(Tile from, Tile to, boolean canFly) {
        final Point enemyPos = from.absPos();
        final Tile enemyTile = modelGrid.getTileAt(enemyPos);
        final Tile goalTile = to;
        
        List<Tile> path = calculatePath(enemyTile, goalTile, canFly);
        if (path == null) {
            return null;
        }
        List<Point> points = new ArrayList<Point>();
        Point p = null;
        
        for (int i = 0, s = path.size(); i < s; i++) {
            Tile pathElem = path.get(i);
            p = pathElem.getPos(Location.CENTER);

            addAndRemoveRedundant(points, p);
            
            if (pathElem == goalTile) { // target tile
                break;
            }
            
            Tile next = path.get(i+1);
            Location exitPoint = getDirection(pathElem, next);
            p = pathElem.getPos(exitPoint);
            
            addAndRemoveRedundant(points, p);
        }
        
        return points;
    }
    
    private static void addAndRemoveRedundant(List<Point> points, Point next) {            
        int pointsSize = points.size();
        
        if (pointsSize > 2) { // test for last point if its redundant
            Point underTest = points.get(pointsSize-1);
            Point beforeUnderTest = points.get(pointsSize-2);
            
            if (isRedundantPoint(underTest, beforeUnderTest, next)) {
                points.remove(pointsSize-1);
            }
        }
        points.add(next);
    }
    
    private static boolean isRedundantPoint(Point underTest, Point before, Point after) {
        double xOrigin = before.getX();
        double yOrigin = before.getY();
        double x1 = underTest.getX();
        double y1 = underTest.getY();
        double x2 = after.getX();
        double y2 = after.getY();
        double distance1 = Point.distance(xOrigin, yOrigin, x1, y1);
        double distance2 = Point.distance(xOrigin, yOrigin, x2, y2);

        // point 1 must be nearer to origin and must not be a duplicate:
        if (distance1 > distance2 || 0.000001 > Math.abs(distance1)) {
            throw new RuntimeException("before: "+before+", after: "+after+", middle="+underTest);
        }

        double deltaX1 = x1 - xOrigin;
        double deltaX2 = x2 - xOrigin;
        double deltaY1 = y1 - yOrigin;
        double deltaY2 = y2 - yOrigin;
        
        double slopeRatio1;
        double slopeRatio2;
        
        if (deltaY1 == 0 && deltaY2 == 0) {
            return true;
        }
        
        slopeRatio1 = deltaX1 / deltaY1;
        slopeRatio2 = deltaX2 / deltaY2;
        
        
        if (0.000001 > Math.abs(slopeRatio1 - slopeRatio2 )) {
            return true;
        }
//        System.out.println("---------------------------------------------------------");
//        System.out.println("AStar.isRedundantPoint() before    (origin)   : "+before);
//        System.out.println("AStar.isRedundantPoint() underTest (point1)   : "+underTest);
//        System.out.println("AStar.isRedundantPoint() after     (point2)   : "+after);
        return false;
    }

    private List<Tile> calc() {
        distancesFromStart.clear();
        possibleTiles.clear();
        previousTiles.clear();
        
        // start with the start tile :-)
        distancesFromStart.put(startTile, Double.valueOf("0"));
        possibleTiles.add(startTile);
        
        if (targetTile == startTile) {
            return Collections.singletonList(startTile);
        }
        
        for (boolean found = false; ! found;) {
            if (possibleTiles.isEmpty()) {
                return null; // no path could be determined!
            }
            
            // choose the nearest, and remove from entered tiles
            Collections.sort(possibleTiles, heuristic);
            final Tile bestGuess = possibleTiles.get(0);
            possibleTiles.remove(bestGuess);
            
            // select possible neighbours of nearest tile
            Set<Tile> neighbours = collectNeighbours(bestGuess);
            
            for (Iterator<Tile> i = neighbours.iterator(); i.hasNext();) {
                Tile neighbour = i.next();
                
                if (handleNeighbour(neighbour, bestGuess)) {
                    found = true;
                    break; // done !
                }
            }
        }
        
        List<Tile> path = collectPath();
        return path;
    }
    
    private List<Tile> collectPath() {
        // note that targetTile and start tile are 
        // both contained in previousTiles
        List<Tile> path = new ArrayList<Tile>();

        for (Tile t = targetTile; ;) {
            path.add(t);
            Tile prevPathElement = previousTiles.get(t);
            t = prevPathElement;

            if (t == startTile) {
                path.add(t);
                break;
            }
        }

        Collections.reverse(path);
        return path;
    }
    
    private Location getDirection(Tile from, Tile to) {
        if (from.row == to.row) {
            if (from.col == to.col) throw new RuntimeException("from="+from+", to="+to);
            if (from.col < to.col)  return Location.RIGHT;
            if (from.col > to.col)  return Location.LEFT;
            
        } else if (from.row > to.row) { // upwards
            if (from.col == to.col) return Location.TOP;
            if (from.col < to.col)  return Location.TOP_RIGHT;
            if (from.col > to.col)  return Location.TOP_LEFT;
        
        } else if (from.row < to.row) {
            if (from.col == to.col) return Location.LOWER;
            if (from.col < to.col)  return Location.LOWER_RIGHT;
            if (from.col > to.col)  return Location.LOWER_LEFT;
        }
        
        throw new RuntimeException("from="+from+", to="+to);
    }

    private Set<Tile> collectNeighbours(Tile t) {
        Set<Tile> result = new HashSet<Tile>(8);
        
        for (int r = t.row-1; r <= t.row+1; r++) {
            if (r < 0 || r >= modelGrid.rows) {
                continue;
            }
            
            for (int c = t.col-1, maxC = t.col+1; c <= maxC; c++) {
                if (c < 0 || c >= modelGrid.cols) {
                    continue;
                }
                if (r == t.row && c == t.col) {
                    continue;
                }
                
                Tile candidate = modelGrid.getTileAt(r, c);

                if ( ! (ignoreBarriers || candidate.isWalkable())) {
                    continue;
                }
                if ( ! ignoreBarriers && wouldCollideDiagonally(t, candidate)) {
                    continue;
                }
                
                result.add(candidate);
            }
        }
        
        return result;
    }
    
    private boolean wouldCollideDiagonally(Tile t, Tile candidate) {
        Location l = getDirection(t, candidate);
        
        if ( ! Location.isDiagonal(l)) {
            return false;
        }
        
        switch (l) {
            case TOP_LEFT: {
                Tile left = modelGrid.getNeighbour(t, Location.LEFT);
                Tile upper = modelGrid.getNeighbour(t, Location.TOP);
                if ( ! left.isWalkable() || ! upper.isWalkable()) {
                    return true;
                }
                break;
            }
            case TOP_RIGHT: {
                Tile upper = modelGrid.getNeighbour(t, Location.TOP);
                Tile right = modelGrid.getNeighbour(t, Location.RIGHT);
                if ( ! upper.isWalkable() || ! right.isWalkable()) {
                    return true;
                }
                break;
            }
            case LOWER_RIGHT: {
                Tile right = modelGrid.getNeighbour(t, Location.RIGHT);
                Tile lower = modelGrid.getNeighbour(t, Location.LOWER);
                if ( ! lower.isWalkable() || ! right.isWalkable()) {
                    return true;
                }
                break;
            }
            case LOWER_LEFT: {
                Tile left = modelGrid.getNeighbour(t, Location.LEFT);
                Tile lower = modelGrid.getNeighbour(t, Location.LOWER);
                if ( ! lower.isWalkable() || ! left.isWalkable()) {
                    return true;
                }
                break;
            }
        }
        
        return false;
    }
    
    private boolean handleNeighbour(Tile hoodie, final Tile prev) {
        final Point prevAbsPos = prev.absPos();
        final double prevWay = distancesFromStart.get(prev).doubleValue();
        
        double distance = hoodie.absPos().distance(prevAbsPos);
        double wayLength = Double.valueOf(distance + prevWay);

        // check if we already entered this tile with a shorter way.
        // only proceed when this tile was not entered yet or
        // on entering by walking a shorter length
        Double oldLength = distancesFromStart.get(hoodie);
        if (oldLength != null && oldLength.doubleValue() < wayLength) {
            return false; 
        }

        // overwrites both mappings when shorter way found: 
        distancesFromStart.put(hoodie, Double.valueOf(wayLength));
        previousTiles.put(hoodie, prev);
        
        if ( ! possibleTiles.contains(hoodie)) {
            possibleTiles.add(hoodie);
        }
        
        if (hoodie == targetTile) {
            return true; // done !
        }
        
        return false;
    }
    
}
