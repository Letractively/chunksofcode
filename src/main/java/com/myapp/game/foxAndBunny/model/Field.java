package com.myapp.game.foxAndBunny.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Field {

    private static final Random RANDOM = new Random();

    private World world;

    private final Position position;
    private List<Position> neighbourhood;
    
    
    public Field(World w, Position position) {
        this.world = w;
        this.position = position;
        neighbourhood = calcNeighbours();
    }
    
    public List<Position> getNeighbourhood() {
        return neighbourhood;
    }
    
    public Position getPosition() {
        return position;
    }

    public boolean hasActor() {
        return world.getPopulation().hasActor(position);
    }

    public World getWorld() {
        return world;
    }

    public Actor getActor() {
        return world.getPopulation().getActor(position);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Field [pos=");
        builder.append(getPosition());
        builder.append(",actor=");
        builder.append(getActor());
        builder.append("]");
        return builder.toString();
    }

    private List<Position> calcNeighbours() {
        return calcNeighbourhood(position, 
                                 world.getRowCount(), 
                                 world.getColCount());
    }
    
    private static List<Position> calcNeighbourhood(Position pos,
                                                    int rows,
                                                    int cols) {
        int row = pos.getRow();
        int col = pos.getCol();
        
        int rowMin =  (row <= 0     )  ?  (0     )  :  (row-1);
        int rowMax =  (row >= rows-1)  ?  (rows-1)  :  (row+1);
        int colMin =  (col <= 0     )  ?  (0     )  :  (col-1);
        int colMax =  (col >= cols-1)  ?  (cols-1)  :  (col+1);

        List<Position> hood = new ArrayList<Position>(8);
        
        for (int r = rowMin; r <= rowMax; r++) {
            for (int c = colMin; c <= colMax; c++) {
                if (r == row && c == col)
                    continue;
                
                hood.add(Position.create(r, c));
            }
        }
        
        return hood;
    }

    /**
     * @param pos
     * @param mustHaveActor
     * @param actorType ignored when ! mustHaveActor
     * @param mustBeFree
     * @param diagonal
     * @return
     */
    private List<Position> neighbours(boolean mustHaveActor,
                                      Class<? extends Actor> actorType,
                                      boolean mustBeFree, 
                                      boolean diagonal) {
        if (mustHaveActor && mustBeFree)
            throw new IllegalArgumentException();
        
        int row = position.getRow();
        int col = position.getCol();
        
        List<Position> hood = new ArrayList<Position>(neighbourhood);

        for (Iterator<Position> itr = hood.iterator(); itr.hasNext(); ) {
            boolean delete = false;
            Position p = itr.next();
            
            if ( ! diagonal && (row != p.getRow() && col != p.getCol())) { 
                delete = true;
            } else {
                Actor a = world.getPopulation().getActor(p);
                if (mustBeFree && a != null) { 
                    delete = true;
                } else if (mustHaveActor) {
                    if (a == null) {
                        delete = true;
                    } else if (actorType != null && ! actorType.isInstance(a)) { 
                        delete = true;
                    }
                }
            }
            
            if (delete)
                itr.remove();
        }
        
        return hood;
    }
    
    private static <T> T pickRandomElement(List<T> elements) {
        int size = elements.size();
        if (size <= 0)
            return null;
        return elements.get(RANDOM.nextInt(size));
    }
    
    public Position getRandomFullNeighbour(Class<? extends Actor> clazz) {
        return pickRandomElement(getFullNeighbours(clazz));
    }

    public Position getRandomFreeNeighbour() {
        return pickRandomElement(getFreeNeighbours());
    }
    
    public List<Position> getFullNeighbours(Class<? extends Actor> clazz) {
        return neighbours(true, clazz, false, true);
    }
    
    public List<Position> getFreeNeighbours() {
        return neighbours(false, null, true, true);
    }

    public List<Position> getNeighbourPositionsFull() {
        return neighbours(false, null, false, true);
    }
}
