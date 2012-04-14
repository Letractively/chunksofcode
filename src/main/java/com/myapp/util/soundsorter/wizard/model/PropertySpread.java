package com.myapp.util.soundsorter.wizard.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.util.songsorter.Algorithms;




/**
 * extracts metadata and counts the matching values from the metadata of a given
 * list of songs.</br> provides a mapping with the absolute occurences, and
 * relative spread of the values.
 * 
 * e.g. a list of songs will be added with a genre extracting routine, the
 * different genres are counted, and the spread of the genres may be listed.
 * 
 * @author andre
 * 
 */
public class PropertySpread implements Serializable
{
    
    private static final long serialVersionUID = -8767983222325897887L;

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[a-z]{3,}\\b");
    
    
    private final String propertyName;
    private final IPropertyExtractor extractor;
    
    

    private Map<String, Integer> absoluteSpread = new HashMap<String, Integer>();
    private Integer totalValueCount = 0;


    /**
     * allocates a new spread with the given property name and the metadata
     * extracting routine.
     * @param propertyName
     * @param extractor
     */
    public PropertySpread(String propertyName, IPropertyExtractor extractor) {
        this.propertyName = propertyName;
        this.extractor = extractor; 
    }


    /**
     * uses the given extractor to collect metadata values of all songs in the
     * given list. calculates the absolute occurences and the relative spread of
     * the metadata values
     * @param songs
     */
    public void applyToSongList(Collection<ISong> songs) {
        absoluteSpread.clear();
        totalValueCount = 0;
        Iterator<ISong> itr = songs.iterator();
        
        while (itr.hasNext()) {
            totalValueCount ++;
            ISong next = itr.next();
            String propertyValue = extractor.extractValue(next);
            
            if (propertyValue == null)
                continue;

            if (absoluteSpread.containsKey(propertyValue)) {
                int countSoFar = absoluteSpread.get(propertyValue).intValue();
                Integer increment = Integer.valueOf(1 + countSoFar);
                absoluteSpread.put(propertyValue, increment);
                
            } else {
                absoluteSpread.put(propertyValue, Integer.valueOf(1));
            }
        }
    }

    
    public String getPropertyName() {
        return propertyName;
    }
    
    public Map<String, Integer> getAbsoluteOccurences() {
        return absoluteSpread;
    }

    public List<String> getValuesOrdered() {
        return listKeysByValues(absoluteSpread);
    }

    public int getOccurenceCount(String genre) {
        Integer i = absoluteSpread.get(genre);
        
        if (i == null) {
            return 0;
        }
        
        return i.intValue();
    }

    public String getMostDominantGenre() {
        if (absoluteSpread.isEmpty())
            return null;

        return listKeysByValues(absoluteSpread).get(0);
    }
    
    public boolean containsValue(String value) {
        return absoluteSpread.containsKey(value);
    }
    
    /**
     * compares this property spread with another property spread the metadata
     * will be compared with fuzzy logic.
     * 
     * @param other
     * @return a double between 0 and 1, 0 means no common entries, 1 means
     *         perfect match
     */
    public MatchSet calcFuzzyEquality(PropertySpread other) {
        return calcFuzzyEquality(this, other);
    }

    /**
     * returns a set of values occuring in both propertyspreads. the comparison
     * will be fuzzy, so there may occur values which are NOT contained in both
     * spreads; each value is contained at least in one spread AND is matching
     * fuzzily to at least one value of the opposite's values.
     * 
     * @param here
     * @param there
     * @return
     */
    public static Set<String> getCommonValues(PropertySpread here, 
                                              PropertySpread there) {
        Set<String> set = new HashSet<String>();
        Iterator<String> itr1 = here.absoluteSpread.keySet().iterator();
        Iterator<String> itr2;
        String valStr1, valStr2;
        
        loop1: while (itr1.hasNext()) {
            valStr1 = itr1.next();
            itr2 = there.absoluteSpread.keySet().iterator();
            
            while (itr2.hasNext()) {
                valStr2 = itr2.next();
                
                if (isFuzzyMatching(valStr1, valStr2)) {
                    set.add(valStr1);
                    set.add(valStr2);
                    continue loop1;
                }
            }
        }
        
        return set;
    }

    /**
     * compares this property spread with another property spread the metadata
     * will be compared with fuzzy logic.
     * 
     * @param ps1
     * @param ps2
     * @return a double between 0 and 1, 0 means no common entries, 1 means
     *         perfect match
     */
    public static MatchSet calcFuzzyEquality(PropertySpread ps1,
                                                PropertySpread ps2) {
        MatchSet matchSet = new MatchSet(ps1, ps2);
        return matchSet;
    }
    
    private static boolean isFuzzyMatching(String s1, String s2) {
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
                
                // allow a fifth of the smallest length of both strings
                // as maximum distance for equality:
                
                int maxDistance = Math.min(group1.length(),
                                           group2.length()
                                  ) / 5;
                int levenstheinDistance = Algorithms.levenshteinDistance(group1, group2);
                
                if (levenstheinDistance <= maxDistance) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private static boolean equalsOrContains(String s1, String s2, int minLength) {
        if (s1.equals(s2) 
                || s1.contains(s2) 
                || s2.contains(s1)) {
            if (s1.length() >= minLength && s2.length() >= minLength) {
                return true;
            }
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
    
    public double getRatio(String genre) {
        Integer occurenceCount = absoluteSpread.get(genre);
        
        if (occurenceCount == 0) {
            return -1d;
        }
        if (totalValueCount.doubleValue() <= 0) {
            return -1d;
        }
        
        return occurenceCount / totalValueCount.doubleValue();
    }
    
    public int getTotalValueCount() {
        return totalValueCount;
    }
    
    private static <K, V extends Comparable<V>> 
                          List<K> listKeysByValues(final Map<K, V> map) {
        List<K> list2 = new ArrayList<K>(map.keySet());
        
        Comparator<K> cmp = new Comparator<K>() {
            @Override
            @SuppressWarnings("unchecked")
            public int compare(K o1, K o2) {
                int comparison = map.get(o1).compareTo(map.get(o2));
                
                if (comparison == 0 
                        && o1 instanceof Comparable 
                        && o2 instanceof Comparable) {
                    try {
                        // reverse, because list will be reversed afterwards:
                        return ((Comparable<K>) o2).compareTo(o1);
                        
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
                return comparison;
            }
        };
        
        Collections.sort(list2, cmp);
        Collections.reverse(list2); // biggest values first
        return list2;
    }
}