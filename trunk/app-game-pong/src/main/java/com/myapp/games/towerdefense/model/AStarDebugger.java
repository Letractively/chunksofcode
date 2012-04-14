package com.myapp.games.towerdefense.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public final class AStarDebugger extends MouseAdapter {
    
    private final AStar aStar;
    private final GameModel model;

    private List<Tile> tilesOfPath = null;
    private List<Point> pointsOfPath = null;
    
    public AStarDebugger(GameModel model) {
        this.model = model;
        this.aStar = new AStar(model);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("AStarDebugger.mouseClicked() ENTERING");
        
        Point mousePos = e.getPoint();
        System.out.println("AStarDebugger.mouseClicked() mouse @ "+mousePos);
        
        Grid g = model.getGrid();
        Tile tile = g.getTileAt(mousePos);
        System.out.println("AStarDebugger.mouseClicked() tile = "+tile);

        if (tile == null) {
            return;
        }
        
        int button = e.getButton();
        System.out.println("AStarDebugger.mouseClicked() button = "+button);
        
        if (button == MouseEvent.BUTTON3) {
            if (g.hasTower(tile)) {
                g.setTower(tile.row, tile.col, null);
            } else {
                g.setTower(tile.row, tile.col, new Tower());
            }
            tilesOfPath = null;    
            pointsOfPath = null;  
            return;
        }
        
        List<Tile> path = aStar.calculatePath(tile, g.getTargetTile(), false);
        
        if (path != null) {
            tilesOfPath = path;
        }

        List<Point> points = aStar.calculatePointPath(mousePos, false);
        if (points != null) {
            pointsOfPath = points;
        }
    }

    public void drawDebugGui(Graphics g) {
        if (tilesOfPath == null) {
            return;
        }

//        drawPathTiles(g);
//        drawDistancesFromStart(g);
        drawDots(g);
    }
    
    void drawDistancesFromStart(Graphics g) {
        Grid grid = model.getGrid();
        
        Map<Tile, Double> distancesFromStart = aStar.distancesFromStart;
        for (Entry<Tile, Double> e : distancesFromStart.entrySet()) {
            Tile t = e.getKey();
            Point absPos = t.absPos();
            Double distance = e.getValue();
            g.setColor(Color.black);
            g.drawString(""+distance.intValue(), 
                         absPos.x + 3, 
                         absPos.y + grid.tileDim / 2);
        }
    }

    void drawPathTiles(Graphics g) {
        Grid grid = model.getGrid();
        
        Map<Tile, Double> distancesFromStart = aStar.distancesFromStart;
        for (Entry<Tile, Double> e : distancesFromStart.entrySet()) {
            Tile t = e.getKey();
            Point absPos = t.absPos();
            
            if (tilesOfPath.contains(t)) {
                g.setColor(Color.yellow);
                if (t != grid.getStartTile() && t != grid.getTargetTile()) {
                    g.fillRect(absPos.x + 1,
                               absPos.y + 1,
                               grid.tileDim - 1,
                               grid.tileDim - 1);   
                }
            }
        }
    }
    
    void drawDots(Graphics g) {
        for (int i = 0, s = pointsOfPath.size(); i < s; i++) {
            Point p = pointsOfPath.get(i);
            int radius = 8;
            int circleY = p.y-radius/2;
            int circleX = p.x-radius/2;

            g.setColor(Color.blue);
            g.fillOval(circleX, circleY, radius, radius);
            g.setColor(Color.black);
            g.drawOval(circleX, circleY, radius, radius);
        }
        
        g.setColor(Color.red);
        
        for (int i = 0, s = pointsOfPath.size(); i < s; i++) {
            Point p = pointsOfPath.get(i);
            if (i >= s-1) {
                break;
            }
            Point next = pointsOfPath.get(i+1);
            g.drawLine(p.x, p.y, next.x, next.y);
        }
    }
}