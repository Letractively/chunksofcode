package com.myapp.games.towerdefense.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.myapp.games.towerdefense.model.Enemy.LifeEnergy;
import com.myapp.games.towerdefense.model.Projectile.Damage;

public class DamageManager {

    private GameModel model;
    private Collection<Projectile> currentProjectiles = new LinkedList<Projectile>();

    public DamageManager(GameModel model) {
        this.model = model;
    }

    /** Put a projectile into the game world. Happens when a weapon is
     * fired.
     * 
     * @param p
     *            the projectile */
    public void shoot(Projectile p) {
        assert !currentProjectiles.contains(p);
        p.getAbsPos().setLocation(p.getFiredFrom().getAbsPos());
        currentProjectiles.add(p);
    }

    private void simulateImpact(Projectile p) {
        assert p.isTargetReached() : p;
        Enemy e = p.getTarget();
        LifeEnergy le = e.getEnergy();
        Damage damge = p.getDamge();
        le.setCurrentEnergy(le.getCurrentEnergy() - damge.getImpactDamage());

        if (le.getCurrentEnergy() < 0)
            e.die(model.getGameTime());
    }

    public void updateGameState(long gameTimeDelta) {
        for (Iterator<Projectile> bullets = currentProjectiles.iterator(); bullets.hasNext();) {
            Projectile p = bullets.next();
            if (p.isImpact()) {
                bullets.remove();
                continue;
            }
            p.move(gameTimeDelta);

            if (p.isTargetReached()) {
                simulateImpact(p);
            }
        }
    }

    public Collection<Projectile> getCurrentProjectiles() {
        return currentProjectiles;
    }
}
