package com.myapp.game.foxAndBunny.model;

import java.util.Random;

public abstract class Animal implements Actor {

    
    protected static final class AnimalParameters {
        private int maxAge;
        private int sexuallyMatureAge;
        private double birthPropability;
        private int maxKidsPerBirth;
        
        public AnimalParameters(int maxAge,
                              int sexuallyMatureAge,
                              double birthPropability,
                              int maxKidsPerBirth) {
            this.maxAge = maxAge;
            this.sexuallyMatureAge = sexuallyMatureAge;
            this.birthPropability = birthPropability;
            this.maxKidsPerBirth = maxKidsPerBirth;
        }
        
        public int getMaxAge() {
            return maxAge;
        }
        public int getMatureAge() {
            return sexuallyMatureAge;
        }
        public double getBirthPropability() {
            return birthPropability;
        }
        public int getMaxKidsPerBirth() {
            return maxKidsPerBirth;
        }
    }
    
    

    protected static final Random RANDOM = new Random(0L);
    
    private int age;
    private boolean alive;
    private World world;
    
    
    protected Animal() {
        age = 0;
        alive = true;
    }
    
    protected Animal(boolean randomAge) {
        alive = true;
        
        if (randomAge) {
            age = RANDOM.nextInt(params().getMaxAge());
        }
    }



    protected abstract AnimalParameters params();
    protected abstract Animal createChild();
    
    
    
    @Override
    public void act() {
        incrementAge();
        
        if (alive) {
            actImpl();
        }
    }
    
    protected void actImpl() {
        reproduce();
        walkToNextField();
    }

    @Override
    public final void setWorld(World world2) {
        this.world = world2;
    }
    
    public final boolean isAlive() {
        return alive;
    }
    
    public final int getAge() {
        return age;
    }

    protected final World getWorld() {
        return world;
    }
    
    public void die() {
        alive = false;

        if (world != null)
            world.getPopulation().removeActor(this);
    }
    
    private void incrementAge() {
        age++;
        
        if (age > params().getMaxAge())
            die();
    }   
    
    protected final void walkToNextField() {
        Position nextFree = getField().getRandomFreeNeighbour();
        
        if (nextFree == null) { // overpopulated
            die();
        } else {
            world.getPopulation().putActor(this, nextFree);
        }
    }

    public void walkToField(Position target) {
        world.getPopulation().putActor(this, target);
    }

    protected final Field getField() {
        Position here = world.getPopulation().getPosition(this);
        return world.getField(here);
    }
    
    protected final void reproduce() {
        if (age < params().getMatureAge())
            return; // no "kinderverzaarer" in this world
        
        int children = 0;
        if (RANDOM.nextDouble() > params().getBirthPropability())
            children = 1 + RANDOM.nextInt(params().getMaxKidsPerBirth());
        
        for (int i = 0; i < children; i++) {
            Position kidHome = getField().getRandomFreeNeighbour();
            
            if (kidHome == null)
                return; // no room
            
            Animal a = createChild();
            a.setWorld(world);
            world.populate(a, kidHome);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" [alive=");
        builder.append(alive);
        builder.append(", age=");
        builder.append(getAge());
        builder.append(", pos=");
        builder.append((world != null) ? world.getPopulation().getPosition(this) : "nowhere");
        builder.append("]");
        return builder.toString();
    }
    
}
