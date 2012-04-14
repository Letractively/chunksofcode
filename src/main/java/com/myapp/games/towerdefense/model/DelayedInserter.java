package com.myapp.games.towerdefense.model;

import java.util.ArrayList;
import java.util.List;

public class DelayedInserter<T> {
    
    private static final class InsertionOffset<T> {
        private final int offsetToNext;
        private final T element;

        public InsertionOffset(int offsetToEnemyBefore, T element) {
            this.offsetToNext = offsetToEnemyBefore;
            this.element = element;
        }

        @Override
        public String toString() {
            return "Insertion[offset=" + offsetToNext + ", e=" + element + "]";
        }
    }
    
    private long nextRelease = -1;
    protected List<InsertionOffset<T>> list = new ArrayList<InsertionOffset<T>>();
    
    public DelayedInserter() {
    }
    
    public long getNextRelease() {
        return nextRelease;
    }
    
    public void addElement(int offset, T element) {
        list.add(new InsertionOffset<T>(offset, element));
    }
    
    public boolean hasElementToInsert(long gameTimeNow) {
        if (isEmpty()) {
            return false;
        }
        return gameTimeNow >= getNextRelease();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public T release(final long gameTimeNow) {
        if (! hasElementToInsert(gameTimeNow)) {
            throw new RuntimeException("must not call when no element is available: list: "+list+", nextRelease: "+nextRelease);
        }
        
        InsertionOffset<T> element = list.remove(0);
        nextRelease = gameTimeNow + element.offsetToNext;
        return element.element;
    }
}
