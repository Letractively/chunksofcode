package com.myapp.mines.model;

/**
thrown by a field when it is being entered if it contains a bomb.
@author andre
 */
@SuppressWarnings("serial")
public class Baaaaaam extends Exception {

    private final Field field;

    /**
    creates a new explosion.
    @param f the reason.
     */
    public Baaaaaam(Field f) {
        super("Field with bomb was entered! " + f);
        field = f;
    }

    /**
    returns the field where the explosion occured.
    @return the field where the explosion occured.
     */
    public Field getField() {
        return field;
    }

}
