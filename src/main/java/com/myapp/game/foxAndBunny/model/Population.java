package com.myapp.game.foxAndBunny.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.myapp.game.foxAndBunny.Logger;




/**
 * manages the population for a world. each actor is connected to a field.
 * 
 * @author andre
 *
 */
public class Population {

    /**
     * saves a chronologic history of population state snapshots.
     * 
     */
    public class StateHistory {
    
        private static final int MAX_IMPRESSIONS_COLLECT = 1920; // full hd pixels
        private final List<PopulationState> states = Collections.synchronizedList(new LinkedList<PopulationState>());
          
    
        void saveSnapShot() {
            synchronized (states) { // called by main thread
                states.add(new PopulationState(world));
                int statesSize = states.size();
                
                // don't let impression collection grow too big:
                if (statesSize >= MAX_IMPRESSIONS_COLLECT+1000) {
                    Iterator<PopulationState> itr = states.iterator();
                    
                    for (int i = 0; i < 1000; i++) {
                        itr.next();
                        itr.remove();
                    }
                    
                    Logger.debug("Population.StateHistory.saveSnapShot() "+
                                 "old state entries deleted. " +
                                 "size before="+statesSize+", " +
                                 "after="+states.size());
                }
            }
        }
        
        public ListIterator<PopulationState> listIterator() {
            return states.listIterator();
        }
        public int size() {
            return states.size();
        }
        public PopulationState get(int index) {
            return states.get(index);
        }
        public List<PopulationState> subList(int fromIndex, int toIndex) {
            return states.subList(fromIndex, toIndex);
        }

        public ListIterator<PopulationState> listIterator(int startIdx) {
            return states.listIterator(startIdx);
        }
    }
    
    /**
     * backs the data of a population state snapshot.
     * needed for the graph to paint the state some steps ago
     */
    public static final class PopulationState {
        private final int step, population, foxes, bunnies, space;

        PopulationState(World world) {
            Population p = world.getPopulation();
            this.step = world.getSteps();
            this.population = p.getSize();
            this.foxes = p.actorCount(Fox.class);
            this.bunnies = p.actorCount(Bunny.class);
            this.space = world.getRowCount() * world.getColCount();
        }

        public Integer getSteps() {return (step);}
        public Integer getPopulation() {return (population);}
        public Integer getFoxes() {return (foxes);}
        public Integer getBunnies() {return (bunnies);}
        public double getFields() {return space;}
    }
    
    private World world;
    private BiMap<Actor, Position> actors = HashBiMap.create();
    private StateHistory history = new StateHistory();
    
    /** for faster calculation in actorCount(Class<? extends Actor>) */
    private Map<Class<? extends Actor>, AtomicInteger> actorCounts = new HashMap<Class<? extends Actor>, AtomicInteger>();
    

    public void init(World w) {
        this.world = w;
    }
    
    public Position getPosition(Field f) {
        return f.getPosition();
    }

    public PopulationState getState() {
        return new PopulationState(world);
    }
    
    public Position getPosition(Actor a) {
        Position p = actors.get(a);
        if (p == null)
            p = Position.NOWHERE;
        return p;
    }

    public Actor getActor(Position p) {
        return actors.inverse().get(p);
    }

    public boolean hasActor(Position position) {
        return null != getActor(position);
    }
    
    public boolean hasActor(Field f) {
        return hasActor(getPosition(f));
    }
    public Set<Actor> getAllActors() {
        return new HashSet<Actor>(actors.keySet());
    }

    public List<Position> getAllActorPositions() {
        return new ArrayList<Position>(actors.inverse().keySet());
    }

    public List<Position> getAllNonActorPositions() {
        List<Position> allFree = world.getAllPositionsList();
        Set<Position> actorsPositions = actors.inverse().keySet();
        List<Position> result = new ArrayList<Position>();
        
        for (Iterator<Position> freeItr = allFree.iterator(); freeItr.hasNext();) {
            Position p = freeItr.next();
            if ( ! actorsPositions.contains(p)) {
                result.add(p);
            }
        }
        
        return result;
    }

    public int getSize() {
        return actors.size();
    }
    
    
    public int actorCount(Class<? extends Actor> clazz) {
        AtomicInteger integer = actorCounts.get(clazz);
        
        if  (integer == null)
            return 0;
        
        return integer.get();
    }

    public boolean containsActor(Actor actor) {
        return actors.containsKey(actor);
    }

    public void putActor(Actor actor, Position pos) {
        if (pos == Position.NOWHERE)
            throw new RuntimeException("actor: "+actor);
        
        if ( ! actors.containsKey(actor))
            incrementCount(actor.getClass());
        
        actors.put(actor, pos);
    }
    

    public void removeActor(Actor actor) {
        if (actors.containsKey(actor))
            decrementCount(actor.getClass());
        
        actors.remove(actor);
    }

    void removeActors() {
        Collection<AtomicInteger> values = actorCounts.values();
        
        for (AtomicInteger i : values) {
            i.set(0);
        }
        
        actors.clear();
    }

    public StateHistory getHistory() {
        return history;
    }
    

    private void incrementCount(Class<? extends Actor> actorType) {
        AtomicInteger count = lazyGet(actorType);
        count.incrementAndGet();
    }
    private void decrementCount(Class<? extends Actor> actorType) {
        AtomicInteger count = lazyGet(actorType);
        count.decrementAndGet();
    }
    private AtomicInteger lazyGet(Class<? extends Actor> actorType) {
        AtomicInteger count = actorCounts.get(actorType);
        if (count == null) {
            count = new AtomicInteger(0);
            actorCounts.put(actorType, count);
        }
        return count;
    }
}
