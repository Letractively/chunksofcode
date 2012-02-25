package com.myapp.util.collections;

import java.util.*;

public final class MultiIterator<T> implements Iterator<T>, Iterable<T> {

    private Iterator<Iterator<T>> itrItr;
    private Iterator<T> currItr = null;
    private boolean done = false;

    public MultiIterator(Iterable<Iterator<T>> iterators) {
        itrItr = iterators.iterator();
    }

    public MultiIterator(final Iterable<T>... iters) {
        this(convert(iters));
    }

    public MultiIterator(T[]... arrays) {
        this(convert(arrays));
    }
    
    private static <T> Iterable<Iterator<T>> convert(Iterable<T>[] iters) {
        Collection<Iterator<T>> c = new ArrayList<Iterator<T>>();
        for (int i = 0; i < iters.length; c.add(iters[i++].iterator()));
        return c;
    }
    
    private static <T> Iterable<Iterator<T>> convert(T[]... arrays) {
        Collection<Iterator<T>> c = new ArrayList<Iterator<T>>();
        List<T> asList;
        for (int i = 0; i < arrays.length; i++) {
            asList = Arrays.asList(arrays[i]);
            c.add(asList.iterator());
        }
        return c;
    }
    
    @Override
    public boolean hasNext() {
        if (done) {
            return false;
        }
        if (currItr == null) {
            return jumpToNextNonEmptyItr();
            
        }
        if (currItr.hasNext()) {
            return true;
        } else {
            return jumpToNextNonEmptyItr();
        }
    }
    private boolean jumpToNextNonEmptyItr() {
        while (itrItr.hasNext()) {
            currItr = itrItr.next();
            
            if (currItr.hasNext()) {
                return true; // hit !
            } else {
                continue;    // try next...
            }
        }
        
        // no iterator has any elements remaining, clean up
        
        done  = true;
        currItr = null;
        itrItr = null;
        
        return false;
    }
    @Override
    public T next() {
        if (done) {
            throw new NoSuchElementException();
        }
        return currItr.next();
    }
    @Override
    public void remove() {
        currItr.remove();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}