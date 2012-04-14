package com.myapp.game.foxAndBunny.model;


public class Bunny extends Animal {
    
    private static final int MAX_AGE = 50;
    private static final int SEXUALLY_MATURE_AGE = 5;
    private static final double BIRTH_PROPABILITY = 0.12;
    private static final int BIRTH_COUNT = 5;
    
    public Bunny() {
        super();
    }
    
    public Bunny(boolean randomAge) {
        super(randomAge);
    }

    @Override
    protected AnimalParameters params() {
        return parameters;
    }

    public static AnimalParameters parameters = 
        new AnimalParameters(MAX_AGE,
                             SEXUALLY_MATURE_AGE,
                             BIRTH_PROPABILITY,
                             BIRTH_COUNT);

    
    @Override
    protected Animal createChild() {
        return new Bunny();
    }
}
