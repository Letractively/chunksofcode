package com.myapp.util.soundsorter.wizard.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author andre
 *
 */
public class MatchSet implements Iterable<Match>
{
    public static final MatchSet FAIL = new MatchSet();
    
    private final double equality;
    private final SortedSet<Match> hits;

    
    private MatchSet() {
        equality = 0d;
        hits = Collections.unmodifiableSortedSet(new TreeSet<Match>());
    }
    
    MatchSet(PropertySpread spread1, PropertySpread spread2) {
        if (spread1 == null) {
            throw new NullPointerException();
        }
        if (spread2 == null) {
            throw new NullPointerException();
        }
        if ( ! spread1.getPropertyName().equals(spread2.getPropertyName())) {
            throw new RuntimeException("dps1: "+spread1.getPropertyName() + ", dps2: " +spread2.getPropertyName());
        }
        
        double ratio;
        SortedSet<Match> matches = new TreeSet<Match>();
        Match bestMatch = null;

        Set<String> valuesSpread1 = spread1.getAbsoluteOccurences().keySet();
        Set<String> valuesSpread2 = spread2.getAbsoluteOccurences().keySet();

        if (valuesSpread1.isEmpty() || valuesSpread2.isEmpty()) {
            equality = FAIL.equality;
            hits = FAIL.hits;
            return;
        }
        
        for (String value1 : valuesSpread1) {
            bestMatch = null;
            
            for (String value2 : valuesSpread2) {
                if ( ! MatchResult.isFuzzyMatching(value1, value2)) {
                    continue;
                }

                ratio = Math.min(spread1.getRatio(value1), spread2.getRatio(value2));
                
                if (ratio <= 0d) {
                    continue;
                }
                if (bestMatch == null || ratio > bestMatch.getMatchRate()) {
                    bestMatch = new Match(value1, value2, ratio);
                }
            }
            
            if (bestMatch != null && bestMatch.getMatchRate() > 0) {
                matches.add(bestMatch);
            }
        }

        matches = uniqueValues(matches);
        
        if (matches.isEmpty()) {
            equality = FAIL.equality;
            hits = FAIL.hits;
            return;
        }
        
        double sum = 0d;
        
        for (Iterator<Match> itr = matches.iterator(); itr.hasNext(); ) {
            sum += itr.next().getMatchRate();
        }
        
        equality = sum;
        hits = Collections.unmodifiableSortedSet(matches);
    }
    
    
    /**
     * removes matches that are equal in ONE of their values. the match with
     * the highest rate will be kept.
     * @param matches
     * @return
     */
    private static SortedSet<Match> uniqueValues(SortedSet<Match> matches) {
        // avoids duplicates like this:
        //    MatchSet [equality=0.0679383, hits = [
        //      Match [matchRate=0.0033463, value1=old metal,   value2=instrumental/metal]
        //      Match [matchRate=0.0060249, value1=heavy metal, value2=instrumental/metal]
        //      Match [matchRate=0.0080324, value1=invention of metal, value2=instrumental/metal]
        //      Match [matchRate=0.0097051, value1=classic metal, value2=instrumental/metal]
        //      Match [matchRate=0.0190765, value1=metal, value2=instrumental/metal]
        //      Match [matchRate=0.0217531, value1=early metal, value2=instrumental/metal]
        //    ]
        SortedSet<Match> uniqueValues = new TreeSet<Match>(matches);
        
        for (Iterator<Match> itr = matches.iterator(); itr.hasNext();) {
            Match a = itr.next();
            
            for (Iterator<Match> itr2 = uniqueValues.iterator(); itr2.hasNext();) {
                Match b = itr2.next();
                if ( ! a.equals(b) && (
                                        a.getValue1().equals(b.getValue1()) 
                                        ||
                                        a.getValue2().equals(b.getValue2())
                                      )) {
                    // remove weaker element
                    if (a.compareTo(b) > 0) {
                        itr2.remove();
                    }
                }
            }
        }
        
        return uniqueValues;
    }
    
    public double getEquality() {
        return equality;
    }
    
    public SortedSet<Match> getHits() {
        return hits;
    }
    
    public boolean isEmpty() {
        return hits.isEmpty();
    }

    @Override
    public Iterator<Match> iterator() {
        return hits.iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MatchSet [equality=");
        builder.append(equality);
        builder.append(", hits = [\n"); 
        for (Match m : hits) builder.append("  ").append(m).append("\n");
        builder.append("]");
        return builder.toString();
    }

}
