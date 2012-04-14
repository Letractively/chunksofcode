package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;

public class Tower {

    private double range = 65.0d;
    private double power = 10.0d;
    
    private Point absPos;
    
    public Tower() {
        this.absPos = new Point();
    }
    
    public Point getAbsolutePosition() {
        return absPos;
    }
    
    public double getRange() {
        return range;
    }

    public void shootAt(Collection<Enemy> inRange) {
        assert ! inRange.isEmpty();
        
        // TODO determine target with a strategy pattern
        Enemy furthes = null;
        
        for (Iterator<Enemy> i = inRange.iterator(); i.hasNext();) {
            Enemy e = i.next();
            if (furthes == null || furthes.getTargetWay() > e.getTargetWay()) {
                furthes = e;
            }
        }
        
        
//        Projectile shot = new Projectile(this, ); CONTINUE HERE!!!
    }
}
