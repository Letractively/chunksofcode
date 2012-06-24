package com.myapp.games.towerdefense;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import com.myapp.games.framework.awt.AWTGameRenderer;
import com.myapp.games.towerdefense.model.AStar;
import com.myapp.games.towerdefense.model.AStarDebugger;
import com.myapp.games.towerdefense.model.DamageManager;
import com.myapp.games.towerdefense.model.Enemy;
import com.myapp.games.towerdefense.model.Grid;
import com.myapp.games.towerdefense.model.Projectile;
import com.myapp.games.towerdefense.model.Tile;
import com.myapp.games.towerdefense.model.Tower;

@SuppressWarnings("serial")
public class TDRenderer extends AWTGameRenderer {

    private static final class GameMouseListener extends MouseAdapter {
        
        private AStar aStar;
        private TowerDefenseGame game;
        private Tile illegalBuildTry = null;

        GameMouseListener(TowerDefenseGame game) {
            this.game = game;
            aStar = new AStar(game.model());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            Point mousePos = e.getPoint();
            System.out.println("GameMouseListener.mouseClicked() mouse @ "+mousePos);
            
            Grid g = game.model().getGrid();
            Tile tile = g.getTileAt(mousePos);
            System.out.println("GameMouseListener.mouseClicked() tile = "+tile);

            if (tile == null) {
                return;
            }
            
            int button = e.getButton();
            System.out.println("GameMouseListener.mouseClicked() button = "+button);
            
            if (button == MouseEvent.BUTTON3) {
                if (g.hasTower(tile)) {
                    g.setTower(tile.row, tile.col, null);
                } else {
                    g.setTower(tile.row, tile.col, new Tower());
                }

                List<Tile> path = aStar.calculatePath(g.getStartTile(), 
                                                      g.getTargetTile(), false);
                if (path == null) {
                    illegalBuildTry = tile;
                    g.setTower(tile.row, tile.col, null);
                }
            }
        }

        Tile getIllegalBuildTry() {
            return illegalBuildTry;
        }
    }


    private AStarDebugger aStarDebugger = null;
    private GameMouseListener gml;
    
    
    public TDRenderer(TowerDefenseGame game) {
        super(game);
        gml = new GameMouseListener(game);
        if (TowerDefenseGame.DEBUG_A_STAR) {
            aStarDebugger = new AStarDebugger(game.model()); 
            addMouseListener(aStarDebugger);
        }
        addMouseListener(gml);
    }

    private TowerDefenseGame game() {
        return (TowerDefenseGame) getGame();
    }
    
    
    @Override
    protected void drawGameObjects(Graphics g) {
        TowerDefenseGame game = game();
        Grid grid = game.model().getGrid();
        
        g.setColor(Color.white);
        g.fillRect(0, 0, grid.cols*grid.tileDim, grid.rows*grid.tileDim);
        
        g.setColor(Color.black);
        g.drawRect(0, 0, grid.cols*grid.tileDim, grid.rows*grid.tileDim);

        Tile start = grid.getStartTile();
        Tile target = grid.getTargetTile();
        
        for (Tile t : grid) {
            Point absPos = t.absPos();
            
            if (start == t) {
                g.setColor(Color.blue);
                g.fillRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
                
            } else if (target == t) {
                g.setColor(Color.red);
                g.fillRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
                
            } else if (t.isWalkable()) {
                g.setColor(Color.white);
                g.fillRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
                
            } else {
                if (t.hasTower()) {
                    g.setColor(Color.darkGray);
                    g.fillRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
                    
                } else {
                    g.setColor(Color.lightGray);
                    g.fillRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
                }
            }
            
            if (t == gml.getIllegalBuildTry()) {
                g.setColor(Color.yellow);
                g.fillRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
            }

            g.setColor(Color.black);
            g.drawRect(absPos.x, absPos.y, grid.tileDim, grid.tileDim);
        }
        
        if (TowerDefenseGame.DEBUG_A_STAR) {
            aStarDebugger.drawDebugGui(g);
        }
        
        for (Enemy e : game.model().getIncoming()) {
            Point ePos = e.getAbsPos();
            int radius = 16;
            int circleX = ePos.x-radius/2;
            int circleY = ePos.y-radius/2;

            g.setColor(Color.green);
            g.fillOval(circleX, circleY, radius, radius);
            
            g.setColor(Color.black);
            g.drawOval(circleX, circleY, radius, radius);
        }

        
        for (Tower t : grid.getTowers()) {
            g.setColor(Color.green);
            int range = (int) Math.round(t.getRange());
            Point tPos = t.getAbsPos();
            g.drawOval(tPos.x-range,
                       tPos.y-range,
                       range*2,
                       range*2);
        }
        
        for (Projectile p : game().model().getBullets()) {
//            if (p.isTargetReached()) {
                g.setColor(Color.cyan);
                Point pPos = p.getFiredFrom().getAbsPos();
                Point tPos = p.getTarget().getAbsPos();
                g.drawLine(pPos.x, pPos.y, tPos.x, tPos.y);
//            }
        }
    }
}
