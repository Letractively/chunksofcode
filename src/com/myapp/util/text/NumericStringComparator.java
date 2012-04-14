package com.myapp.util.text;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * respects the numeric value of grouped digits in compared strings. <br>
 * <code>[ s-1-tring, s-2-tring, s-10-tring, s-99-tring, s-100-tring ]</code>
 * 
 * @author andre
 *
 */
public class NumericStringComparator implements Comparator<String> {
    
    private boolean ignoreCase = false;

    public NumericStringComparator() {
        this(false);
    }
    
    public NumericStringComparator(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    public boolean isIgnoreCase() {
        return ignoreCase;
    }
    
    public int compare(String o1, String o2) {
        // avoid wrong results when another thread changes ignoeCase during the operation
        final boolean _ignCase = ignoreCase; 
        
        StringBuilder s1, s2;
        int i1 = 0, i2 = 0, l1 = o1.length(), l2 = o2.length(), cmp;
        
        for (char c1, c2; i1 < l1 && i2 < l2;) {
            c1 = o1.charAt(i1);
            c2 = o2.charAt(i2);

            if (digit(c1) && digit(c2)) { // collect remaining digits and cmp them:
                for (s1 = new StringBuilder(); i1 < l1 && digit(c1 = o1.charAt(i1)); i1++)
                    s1.append(c1);
                
                for (s2 = new StringBuilder(); i2 < l2 && digit(c2 = o2.charAt(i2)); i2++)
                    s2.append(c2);
                
                if (0 != (cmp = compareNumberStrings(s1.toString(), s2.toString())))
                    return cmp;
                
                continue;
            }
            
            if (equalChars(c1, c2, _ignCase)) {
                i1++; i2++;
                continue;
            }
            
            return c1 - c2;
        }

        // let's say long strings come after the short ones.
        return (i1 == l1-1)  ?  (i2 == l2-1 ? 0 : 1)  :  -1;
    }
    
    private static int compareNumberStrings(String s1, String s2) {
        BigInteger bi1 = new BigInteger(s1);
        BigInteger bi2 = new BigInteger(s2);
        return bi1.compareTo(bi2);
    }
    
    private static boolean equalChars(char c1, char c2, boolean ignCase) {
        if (ignCase) 
            return Character.toLowerCase(c1) == Character.toLowerCase(c2);
        return c1 == c2;
    }
    
    private static boolean digit(char c) {
        switch (c) {  // fall-through
            case '1': case '0':  // (guess they occur most often)
            case '2':  case '3':  case '4':  case '5': 
            case '6':  case '7':  case '8':  case '9':  
                return true;
            default:  
                return false;
        }
    }
}