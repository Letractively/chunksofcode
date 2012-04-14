package com.myapp.util.format;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Util
{


    public static final class ReverseComparator<T extends Comparable<T>> implements Comparator<T>  
    {
        @Override
        public int compare(T o1, T o2) {
            return (-1) * o1.compareTo(o2);
        }
    }

    
    public static final class ReverseComparator2<T> implements Comparator<T>  
    {
        private Comparator<T> toInvert;
        
        public ReverseComparator2(Comparator<T> toInvert) {
            this.toInvert = toInvert;
        }
        
        @Override
        public int compare(T o1, T o2) {
            return (-1) * toInvert.compare(o1, o2);
        }
    }
    
    
    
    private Util() {
    }

    public static final long now() {
        return System.currentTimeMillis();
    }
    

    public static String getTwoDigitDoubleString(double value) {
        return getNDigitsDoubleString(value, 2);
    }

    public static String getNDigitsDoubleString(double value, int n) {
        String s = Double.toString(value);
        Matcher m = Pattern.compile("[-]?[0-9]+\\.([0-9]{0,"+n+"})").matcher(s);
        boolean found = m.find();
        if ( ! found) throw new RuntimeException(s);
       
        StringBuilder sb = new StringBuilder(m.group());
        for (int append0 = n - m.group(1).length(); append0-- > 0; sb.append('0'));
        
        return sb.toString();
    }
    
    public static double getSeconds(long startMillis, long endMillis) {
        if (startMillis > endMillis) {
            throw new RuntimeException("startMillis: " + startMillis + ", endMillis: " + endMillis);
        }
        
        Long durationMillis = Long.valueOf(endMillis - startMillis);
        return durationMillis.doubleValue() / 1000d;
    }
    
    public static double getSecondsToNow(long startMillis) {
        long endMillis = now();
        if (startMillis > endMillis) {
            throw new RuntimeException("startMillis: " + startMillis + ", endMillis: " + endMillis);
        }
        
        Long durationMillis = Long.valueOf(endMillis - startMillis);
        return durationMillis.doubleValue() / 1000d;
    }
    
    public static String timespanStrToNow(long startMillis, int digitsAfterPoint) {
        double seconds = getSeconds(startMillis, now());
        return getNDigitsDoubleString(seconds, digitsAfterPoint);
    }
    
    public static String timespanStr(long startMillis, long endMillis, int digitsAfterPoint) {
        double seconds = getSeconds(startMillis, endMillis);
        return getNDigitsDoubleString(seconds, digitsAfterPoint);
    }
    
    public static <T> Set<T> getCommonElements(Collection<T> c1, Collection<T> c2) {
        if (c1 == null || c2 == null || c1.isEmpty() || c2.isEmpty()) {
            Set<T> empty = Collections.emptySet();
            return empty;
        }
        
        Set<T> result = new HashSet<T>(c1);
        result.retainAll(c2);
        return result;
    }
    
    public static String squeeze(String s, int maxLen) {
        int sLen = s.length();
        if (sLen <= maxLen) {
            return s;
        }
        
        String middlePart = "...";
        int middlePartLen = middlePart.length();
        int sChars = maxLen - middlePartLen;
        int firstHalfLen = sChars / 2;
        int secondHalfLen = sChars - firstHalfLen;
        
        StringBuilder buf = new StringBuilder(maxLen);
        buf.append(s.substring(0, firstHalfLen));
        buf.append(middlePart);
        buf.append(s.substring(sLen - secondHalfLen));
        return buf.toString();
    }

    public static String hackToLength(final String s, final int i) {
        final int len = s.length();
        
        if (len == i) {
            return s;
        }

        StringBuilder result = new StringBuilder(i);
        
        if (i > len) {
            result.append(s);
            
            for (int n = len; n < i; n++) {
                result.append(' ');
            }
        
        } else {
            result.append(s.substring(0, i-4));
            result.append(" ...");
        }
        
        return result.toString();
    }

    
    public static void log(String msg, long startMillis) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        for (int i = 70 - msg.length(); i-- > 0; sb.append(' '));
        sb.append(" time: ");
        String timespan = timespanStrToNow(startMillis, 3);
        sb.append(timespan);
        sb.append(" seconds");
        System.out.println(sb.toString());
    }
}
