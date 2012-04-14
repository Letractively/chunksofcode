package com.myapp.games.towerdefense.model;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;

public class Enemy {
    
    private final Point absPos = new Point();
    private boolean canFly = false;
    private List<Point> waypoints = null;
    private double distancePerSecond = 50.0;
    private boolean targetReached = false;
    private double roundingError = 0.0;
    private double totalTargetDistance;
    private double walkedSoFar;
    
    public void setWaypoints(Point startPos, List<Point> waypoints) {
        this.waypoints = waypoints;
        this.absPos.setLocation(startPos);
        
        double totalDistance = 0.0d;
        Point a = absPos, b = null; 
        
        for (Iterator<Point> i = waypoints.iterator(); i.hasNext();) {
            b = i.next();
            double distance = a.distance(b);
            totalDistance += distance;
            a = b;
        }
        
        this.totalTargetDistance = totalDistance;
        this.walkedSoFar = 0.0d;
    }

    public Point absPos() {
        return absPos;
    }
    
    public boolean canFly() {
        return canFly;
    }

    public void move(long gameTimeDelta) {
        assert ! targetReached;
        if (targetReached) {
            return;
        }
        
        double restTime = gameTimeDelta;
        
        do {
            Point oldAbsPos = new Point(absPos());
            Point next = waypoints.get(0);
            
            final double distanceToNext = next.distance(absPos);
            final double walkPerMilli = distancePerSecond / 1000.0d;
            final double walkDistance = restTime * walkPerMilli + roundingError;
            
            if (walkDistance > distanceToNext) { // next point was reached!
                absPos.setLocation(next);
                double timeNeeded = distanceToNext / walkPerMilli;
                walkedSoFar += distanceToNext;
                restTime -= timeNeeded;
                roundingError = 0.0d;
                waypoints.remove(0);
                
                if (waypoints.isEmpty()) { // last point reached!
                    targetReached = true;
                    break;
                }
            
            } else { // walk as far as possible to the next point
                double ratio = walkDistance / distanceToNext;
                int xOffset = (int) Math.round((next.x - absPos.x) * ratio);
                int yOffset = (int) Math.round((next.y - absPos.y) * ratio);
                absPos.translate(xOffset, yOffset);
                Point newAbsPos = new Point(absPos);
                double distance = oldAbsPos.distance(newAbsPos);
                walkedSoFar += walkDistance;
                roundingError = walkDistance - distance;
                // System.out.println("Enemy.move() ahead to point: "+nextPoint);
                // System.out.println("Enemy.move() walkDistance:   "+walkDistance);
                // System.out.println("Enemy.move() walked:         "+distance);
                // System.out.println("Enemy.move() roundingError:  "+roundingError);
                // System.out.println("Enemy.move() ");
                break;
            }
            
        } while (restTime > 0.0d);
    }
    
    public boolean isTargetReached() {
        return targetReached;
    }

    public double getTargetWay() {
        return totalTargetDistance - walkedSoFar;
    }
}
