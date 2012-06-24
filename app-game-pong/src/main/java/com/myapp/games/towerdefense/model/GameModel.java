package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.myapp.games.framework.IGameTimeCallback;
import com.myapp.games.towerdefense.model.Tile.Location;


public final class GameModel {
    

    private boolean gameOver = false;

    private Grid grid;
    private Level level;
    private Wave currentWave = null;
    private DamageManager damageManager;
    private List<Enemy> incoming = new ArrayList<Enemy>();
    private AStar aStar = null;
    private IGameTimeCallback timeCallback;

    public GameModel(int gridWidth, int rows, int cols, IGameTimeCallback cb) {
        grid = new Grid(rows, cols, gridWidth);
        damageManager = new DamageManager(this);
        aStar = new AStar(this);
        level = new TestLevel(rows, cols);
        level.setUpModel(grid);
        timeCallback = cb;
    }

    
    public boolean isGameOver() {
        return gameOver;
    }

    /** @param gameTimeNow
     * @param gameTimeDelta */
    public void updateState(long gameTimeNow, long gameTimeDelta) {
        if (currentWave == null && level.hasElementToInsert(gameTimeNow)) {
            currentWave = level.release(gameTimeNow);
        }

        // insert enemies if there is time for a release:
        if (currentWave != null && currentWave.hasElementToInsert(gameTimeNow)) {
            Enemy enemy = currentWave.release(gameTimeNow);
            Tile start = grid.getTileAt(level.getStartRow(),
                                        level.getStartCol());
            Point startPos = start.getPos(Location.CENTER);
            List<Point> path = aStar.calculatePointPath(enemy);
            assert path != null;
            enemy.setWaypoints(startPos, path);
            incoming.add(enemy);
        }


        // shoot with towers:
        for (Tower t : grid.getTowers()) {
            Point pos = t.getAbsPos();
            double range = t.getRange();
            Collection<Enemy> inRange = getEnemiesInPosition(pos, range);
            if (inRange == null || inRange.isEmpty()) {
                continue;
            }
            
            Projectile at = t.shootAt(inRange, getGameTime());
            if (at != null) {
                damageManager.shoot(at);
            }
        }
        
        damageManager.updateGameState(gameTimeDelta);

        // move all enemies
        for (Iterator<Enemy> iterator = incoming.iterator(); iterator.hasNext();) {
            Enemy enemy = iterator.next();
            
            if (enemy.getEnergy().isDead()) {
                iterator.remove();
                // TODO apply to score
                continue;
            }
            
            enemy.move(gameTimeDelta);

            if (enemy.isTargetReached()) {
                iterator.remove();
                // TODO apply to score
            }
        }

        if (currentWave != null && currentWave.isEmpty()) {
            currentWave = null;
        }
    }

    public DamageManager getDamageManager() {
        return damageManager;
    }
    
    public Collection<Enemy> getEnemiesInPosition(Point towerPos,
                                                  double towerRange) {
        List<Enemy> enemies = null;

        for (Enemy e : incoming) {
            Point enemyPos = e.getAbsPos();
            double distance = towerPos.distance(enemyPos);
            if (distance <= towerRange) {
                if (enemies == null) {
                    enemies = new ArrayList<Enemy>();
                }
                enemies.add(e);
            }
        }

        return enemies;
    }

    public Grid getGrid() {
        return grid;
    }

    public List<Enemy> getIncoming() {
        return Collections.unmodifiableList(incoming);
    }

    public long getGameTime() {
        return timeCallback.getGameTime();
    }


    public Collection<Projectile> getBullets() {
        return damageManager.getCurrentProjectiles();
    }
}
