package com.myapp.game.foxAndBunny.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.myapp.game.foxAndBunny.Logger;


/**
 * represents a world consisting of a set of fields and a population
 * 
 * @author andre
 *
 */
public class World {
    
    private static Random RANDOM = new Random();
    
    private final int rows, cols, fieldCount;
    private final Field[][] fields;
    
    /** unmodifiable, for iterating only */
    private final List<Position> positionList; 

    private final Population population = new Population();
    int steps = 0;
    
    public World() {
        this(70, 70);
    }
    
    public World(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        fields = new Field[rows][cols];
        fieldCount = rows * cols;
        List<Position> positionListTmp = new ArrayList<Position>(fieldCount);
        
        for (int r = 0; r < fields.length; r++) {
            for (int c = 0; c < fields[0].length; c++) {
                Position p = Position.create(r, c);
                fields[r][c] = new Field(this, p);
                positionListTmp.add(p);
            }
        }

        positionList = Collections.unmodifiableList(positionListTmp);
        population.init(this);
    }
    
    public void letActorsAct() {
        for (Actor a : population.getAllActors()) {
            a.act();
        }
        population.getHistory().saveSnapShot();
        steps++;
    }

    public Field getField(int row, int col) {
        return fields[row][col];
    }
    
    public Field getField(Position p) {
        if (p == Position.NOWHERE)
            throw new RuntimeException();
        return getField(p.getRow(), p.getCol());
    }
    
    /**
     * removes all actors, inits the worlds default population
     */
    public void initDefaultPopulation() {
        Logger.info("World.initDefaultPopulation() entering...");
        steps = 0;
        population.removeActors();

        int bunnies = Math.max(Double.valueOf(fieldCount * 0.5).intValue(), 1);
        int foxes = Math.max(Double.valueOf(fieldCount * 0.001).intValue(), 1);
        
        Logger.info("World.initDefaultPopulation() " +
        		    "create "+bunnies+" bunnies and "+foxes+" foxes...");
        
        for (int i = 0; i < fieldCount; i++) {
            if (i < foxes) populate(new Fox());
            if (i < bunnies) populate(new Bunny());
        }
        
        Logger.info("World.initDefaultPopulation() done!");
    }
    
    public boolean populate(Actor actor, Position position) {
        if (isOverPopulated())
            return false;
        if (population.containsActor(actor)) 
            throw new RuntimeException("actor already in world: "+actor);
        if (position == null)
            position = getRandomPositionFree();
        if (position == null)
            return false; // no room
        
        if ( ! population.hasActor(position)) {
            actor.setWorld(this);
            population.putActor(actor, position);
            return true;
        }
        
        return false;
    }

    public boolean populate(Actor a) {
        return populate(a, null);
    }

    public boolean isOverPopulated() {
        return population.getSize() >= fieldCount;
    }
    
    public int getRowCount() {return rows;}
    public int getColCount() {return cols;}
    public int getSteps() {return steps;}
    public Population getPopulation() {return population;}
    List<Position> getAllPositionsList() {return positionList;}
    
    public Position getRandomPosition()        {return randomPos(false, false);}
    public Position getRandomPositionFree()    {return randomPos(false, true); }
    public Position getRandomPositionNonFree() {return randomPos(true,  false);}

    private Position randomPos(boolean mustHaveActor, boolean mustBeFree) {
        if (mustHaveActor && mustBeFree)
            throw new IllegalArgumentException();
        List<Position> pl;

        if (mustBeFree) {
            pl = population.getAllNonActorPositions();
        } else if (mustHaveActor) {
            pl = population.getAllActorPositions();
        } else {
            pl = positionList;
        }
        
        if (pl.isEmpty())
            return null;
        
        return pickRandomElement(pl);
    }
    
    private static <T> T pickRandomElement(List<T> elements) {
        int size = elements.size();
        if (size <= 0)
            return null;
        return elements.get(RANDOM.nextInt(size));
    }
}
