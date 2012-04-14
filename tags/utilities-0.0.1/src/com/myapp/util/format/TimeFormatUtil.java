package com.myapp.util.format;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeFormatUtil {


    private static final Format TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    
    
    public static String getDateLabel(long date) {
        return TIME_FORMAT.format(new Date(date));
    }
    
    public static String getDateLabel(Date date) {
        return TIME_FORMAT.format(date);
    }
    

    /**
     * @param seconds
     * @return a string which represents a floating point number with max. 2
     *         decimals
     */
    public static String formatTimeTo2Digits(double seconds) {
        String timeString = Double.toString(seconds);
        
        int dotPos = timeString.indexOf(".");
        if (dotPos < 0)
            return timeString;

        Pattern p = Pattern.compile("([0-9]{1,})\\.([0-9]{1,})");
        Matcher m = p.matcher(timeString);
        if ( ! m.matches())
            throw new RuntimeException("WARNING: pattern '" + p.pattern() + "' " +
                                       "did not match input '" + timeString + "'");
        
        StringBuilder b = new StringBuilder(m.group(1));
        b.append('.');
        String afterCommaDigits = m.group(2);
        
        if (afterCommaDigits.length() > 2)
            b.append(m.group(2).substring(0, 2));
        else
            b.append(afterCommaDigits);
        
        return b.toString();
    }
    
    public static String getTimeLabel(long millis) {
        double seconds = Long.valueOf(millis).doubleValue() / 1000.0;
        return getTimeLabel(seconds);
    }
    
    public static String getTimeLabel(double seconds) {
        return getTimeLabel(seconds, true);
    }
    
    public static String getTimeLabel(double seconds, boolean longFormat) {
        int secs = new Double(Math.floor(seconds)).intValue();
        
        int hours = secs / 3600;
        secs = secs % 3600;
        
        int minutes = secs / 60;
        secs = secs % 60;

        StringBuilder b = new StringBuilder();
        
        if (hours > 0) {
            if (hours < 10) 
                b.append(0);

            b.append(hours);
            b.append(longFormat ? "h " : ":");
        }
        
        if (minutes < 10)
            b.append(0);
        
        b.append(minutes);
        b.append(longFormat ? "m " : ":");
        
        if (secs < 10)
            b.append(0);
        
        b.append(secs);

        if (longFormat) {
            int hundreths = Double.valueOf((seconds - secs) * 100).intValue();
            b.append(".");
            b.append(hundreths);
            b.append("s");
        }
            
        return b.toString();
    }
    
}
