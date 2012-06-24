package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;

public class Tower {

    private final class TestProjectile extends Projectile {
        private TestProjectile(Enemy target, Tower firedFrom) {
            super(target, firedFrom);
        }
    }

    private double range = 65.0d;
    private long fireInterval = 500;
    private Long lastFireTime = null;
    private ISelectionStrategy selectionStrategy = new ISelectionStrategy() {
        
        @Override
        public Enemy selectEnemy(Collection<Enemy> inRange) {
            Enemy furthest = null;
            for (Iterator<Enemy> i = inRange.iterator(); i.hasNext();) {
                Enemy e = i.next();
                if (furthest == null || furthest.getTargetWay() > e.getTargetWay()) {
                    furthest = e;
                }
            }
            if (furthest == null) {
                return null;
            }
            return furthest;
        }
    };;;
    private Point absPos;

    public Tower() {
        this.absPos = new Point();
    }

    public Point getAbsPos() {
        return absPos;
    }

    public double getRange() {
        return range;
    }

    public Projectile shootAt(Collection<Enemy> inRange, long gameTime) {
        assert !inRange.isEmpty();
        if (lastFireTime == null || gameTime > (fireInterval + lastFireTime)) {
            Enemy selected = selectionStrategy.selectEnemy(inRange);
            if (selected != null) {
                lastFireTime = gameTime;
                return createProjectile(selected);
            }
        }
        return null;
    }

    protected Projectile createProjectile(Enemy e) {
        return new TestProjectile(e, this);
    }
    
    interface ISelectionStrategy {
        public Enemy selectEnemy(Collection<Enemy> inRange);
    }
}
