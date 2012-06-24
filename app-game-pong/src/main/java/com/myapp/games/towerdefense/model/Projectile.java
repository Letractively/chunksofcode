package com.myapp.games.towerdefense.model;

import java.awt.Point;

public abstract class Projectile {
    
    public static class Damage {

        private double impactDamage = 20d;
        // TODO implement later
        private Double splashDamage = null;

        public double getImpactDamage() {
            return impactDamage;
        }

        void setImpactDamage(double impactDamage) {
            this.impactDamage = impactDamage;
        }

        public Double getSplashDamage() {
            return splashDamage;
        }

        void setSplashDamage(Double splashDamage) {
            this.splashDamage = splashDamage;
        }

    }

    private Enemy target;
    private Tower firedFrom;
    private double distancePerSecond = 20000000;
    private final Point absPos = new Point();
    private boolean targetReached = false;
    private boolean impact = false;
    private Damage damage = new Damage();

    public Projectile(Enemy target, Tower firedFrom) {
        super();
        this.target = target;
        this.firedFrom = firedFrom;
    }

    public void move(long gameTimeDelta) {
        assert !targetReached;
        if (targetReached) {
            return;
        }

        Point next = target.getAbsPos();
        final double distanceToTarget = next.distance(absPos);
        final double walkPerMilli = distancePerSecond / 1000.0d;
        final double walkDistance = gameTimeDelta * walkPerMilli;

        if (walkDistance > distanceToTarget) { // next point was reached!
            targetReached = true;
            absPos.setLocation(next);
            impact = true;
            return;
        }

        // walk as far as possible to the target
        double ratio = walkDistance / distanceToTarget;
        int xOffset = (int) Math.round((next.x - absPos.x) * ratio);
        int yOffset = (int) Math.round((next.y - absPos.y) * ratio);
        absPos.translate(xOffset, yOffset);
    }

    public Point getAbsPos() {
        return absPos;
    }

    public Enemy getTarget() {
        return target;
    }

    public Tower getFiredFrom() {
        return firedFrom;
    }

    public boolean isTargetReached() {
        return targetReached;
    }

    public Damage getDamge() {
        return damage;
    }
    
    public boolean isImpact() {
        return impact;
    }

}
