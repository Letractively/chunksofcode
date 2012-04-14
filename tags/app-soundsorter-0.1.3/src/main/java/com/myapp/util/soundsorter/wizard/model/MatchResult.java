package com.myapp.util.soundsorter.wizard.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.util.songsorter.Algorithms;

public class MatchResult implements Iterable<MatchSet>
{
    public static final MatchResult FAIL = new MatchResult();
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[a-z]{3,}\\b");
    
    private final Map<String, MatchSet> matchesByFlavour;
    private final String bestMatchFlavour;

    
    private MatchResult() {
        matchesByFlavour = Collections.unmodifiableMap(new HashMap<String, MatchSet>());
        bestMatchFlavour = null;
    }
    
    public MatchResult(SongListMeta meta1, SongListMeta meta2) {
        Set<String> commonFlavours = meta1.getCommonFlavours(meta2);
        Map<String, MatchSet> matchSets;
        matchSets = new HashMap<String, MatchSet>(commonFlavours.size());
        double bestMatch = -1d;
        String bestMatchFlavor = null;
        
        for (String flavour : commonFlavours) {
            PropertySpread spread1 = meta1.getSpread(flavour);
            PropertySpread spread2 = meta2.getSpread(flavour);
            MatchSet matchSet = new MatchSet(spread1, spread2);
            double equality = matchSet.getEquality();
            
            if (bestMatch < equality) {
                bestMatch = equality;
                bestMatchFlavor = flavour;
            }
            
            matchSets.put(flavour, matchSet);
        }
        
        this.bestMatchFlavour = bestMatchFlavor;
        this.matchesByFlavour = Collections.unmodifiableMap(matchSets);
    }

    public String getBestMatchFlavour() {
        return bestMatchFlavour;
    }
    
    public double getHighestMatchValue() {
        if (bestMatchFlavour == null)
            return -1;
        
        MatchSet best = matchesByFlavour.get(bestMatchFlavour);
        if (best == null)
            return -1;
        
        return best.getEquality();
    }
    
    public boolean containsFlavour(String flavour) {
        return matchesByFlavour.containsKey(flavour);
    }

    public MatchSet get(String flavour) {
        return matchesByFlavour.get(flavour);
    }

    public Set<String> flavours() {
        return matchesByFlavour.keySet();
    }

    public Collection<MatchSet> values() {
        return matchesByFlavour.values();
    }

    @Override
    public Iterator<MatchSet> iterator() {
        return values().iterator();
    }
    
    
    

    static boolean isFuzzyMatching(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        
        if (equalsOrContains(s1, s2, 5)) {
            return true;
        }
        // WORD_PATTERN = Pattern.compile("\\b[a-z]{3,}\\b");
        Matcher matcher1 = WORD_PATTERN.matcher(s1);
        Matcher matcher2 = WORD_PATTERN.matcher("foo");
        
        while (matcher1.find()) {
            String group1 = matcher1.group();
            
            if (excludeWord(group1)) {
                continue;
            }
            
            matcher2.reset(s2); // init matcher2
            
            while (matcher2.find()) {
                String group2 = matcher2.group();

                if (excludeWord(group2)) {
                    continue;
                }
                
                if (equalsOrContains(group1, group2, 4)) {
                    return true;
                }

                int distance = Algorithms.levenshteinDistance(group1, group2);
                // allow a fifth of the smallest length of both strings
                // as maximum distance for equality:
                int maxDistance = Math.min(group1.length(), group2.length());
                maxDistance /= 5;
                
                if (distance <= maxDistance) {
                    return true;
                }
            }
        }
        
        return false;
    }

    
    private static boolean equalsOrContains(String s1, String s2, int minLength) {
        if ((s1.equals(s2) || s1.contains(s2) || s2.contains(s1))
                   && (s1.length() >= minLength && s2.length() >= minLength)) {
                return true;
        }
        
        return false;
    }

    private static boolean excludeWord(String split) {
        if (split.length() <= 1
                    || split.equals("the")
                    || split.equals("der")
                    || split.equals("die")
                    || split.equals("and")
                    || split.equals("das")
                    || split.equals("feat")
                    || split.equals("ft")
                    || split.equals("vs")) {
                return true;
            }
        return false;
    }
}
