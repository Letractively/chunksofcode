package com.myapp.videotools.subtitles.srtshift;


import java.util.*;
import java.io.*;
import java.util.regex.*;

public class SrtShifter {
    
    private static final String timeParseRegex = 
         "(?x)"+
         "^ \\s* "+
         "( \\d\\d : \\d\\d : \\d\\d , \\d\\d\\d) "+ // g1
         "\\s* --> \\s*"+
         "( \\d\\d : \\d\\d : \\d\\d , \\d\\d\\d) "+ // g2
         "\\s* $";
    private static final String elementParseRegex = 
                            "(?x) (\\d\\d) : (\\d\\d) : (\\d\\d) , (\\d\\d\\d)";
    private static final String indexLineRegex = "(?x) ^ \\s* (\\d+) \\s* $";

    private Matcher timeLineMatcher = Pattern.compile(timeParseRegex).matcher("foo");
    private Matcher timeElementMatcher = Pattern.compile(elementParseRegex).matcher("foo");
    private Matcher indexLineMatcher = Pattern.compile(indexLineRegex).matcher("foo");
    
    private InputStream input;
    private PrintStream output;
    
    private BufferedReader reader;
    private double offsetSeconds;
        
    public SrtShifter(InputStream in, PrintStream out) {
        this.input = in;
        this.output = out;
    }

    /** The Input looks like:
      *<pre>
      * 1
      * 00:00:02,514 --> 00:00:05,631
      * Unsere Kurzfilmvorfürung
      * 
      * 2
      * 00:00:26,114 --> 00:00:29,948
      * Die CRIMSON LEBENSVERSICHERUNG
      * 
      * 3
      * 00:00:46,754 --> 00:00:49,222
      * Sehen Sie! Steuerbord!
      *</pre>
     * @throws Exception 
      */
    public void shift(double offsetSeconds2) throws Exception {
        this.offsetSeconds = offsetSeconds2;
        reader = new BufferedReader(new InputStreamReader(input));

        String line = null;
        int currentIndex = -1;
        
        while (null != (line = reader.readLine())) {
            if (indexLineMatcher.reset(line).matches()) {
                int i = Integer.parseInt(indexLineMatcher.group(1));
                if (currentIndex >= 0 && i <= currentIndex) {
                    throw new Exception("i: "+i+", currentIndex: "+currentIndex);
                }
                currentIndex = i;
                System.err.printf("currentIndex: '%s'\n", currentIndex);
                output.println(i);
                handleBlock();
                output.println();
            }
        }
    }
    
    private void handleBlock() throws IOException  {
        String line = reader.readLine();
        if (! timeLineMatcher.reset(line).matches()) {
            throw new RuntimeException("line did not match time pattern! " +
        		"expected sth like:\n"+timeParseRegex+"\nbut got:\n'"+line+"'");
        }
        String startTimeStr = timeLineMatcher.group(1);
        String endTimeStr = timeLineMatcher.group(2);

        System.err.printf("start: '%s', end: '%s'\n", startTimeStr, endTimeStr);
        double start = parseTimeElement(startTimeStr);
        double end = parseTimeElement(endTimeStr);

        System.err.printf("start: '%s', end: '%s'\n", 
                          formatTime(start, 3), 
                          formatTime(end, 3));

        start += offsetSeconds;
        end += offsetSeconds;

        String shiftedStart = serializeTimeElement(start);
        String shiftedEnd = serializeTimeElement(end);
        
        output.print(line.substring(0, timeLineMatcher.start(1)));
        output.print(shiftedStart);
        output.print(line.substring(timeLineMatcher.end(1), 
                                    timeLineMatcher.start(2)));
        output.print(shiftedEnd);
        output.print(line.substring(timeLineMatcher.end(2)));
        output.println();
        
        boolean didWriteText = false;
        while (null != (line = reader.readLine())) {
            if (line.trim().isEmpty()) { // indicates end of block
                if (! didWriteText) {
                    throw new RuntimeException("expecting text of subtitle!");
                }
                return;
            }
            output.println(line);
            didWriteText = true;
        }
    }

    //  e.g.:    01:42:11,354     01:42:15,233
    private static String serializeTimeElement(double start) {
        final long rounded = Math.round(start * 1000d);
        final long wholeSeconds = rounded / 1000L;
        final long hh = wholeSeconds / 3600L;
        final long mm = (wholeSeconds % 3600L) / 60L;
        final long ss = wholeSeconds % 60L;
        final long fractions = rounded % 1000L;
        
        StringBuilder result = new StringBuilder(12);
        if (hh < 10) result.append('0');
        result.append(hh);
        result.append(':');
        if (mm < 10) result.append('0');
        result.append(mm);
        result.append(':');
        if (ss < 10) result.append('0');
        result.append(ss);
        result.append(',');
        if (fractions < 100) result.append('0');
        if (fractions < 10) result.append('0');
        result.append(fractions);
        return result.toString();
    }

    private double parseTimeElement(String timeElement) {
        if (! timeElementMatcher.reset(timeElement).matches()) {
            throw new RuntimeException(timeElement); // must match !
        }

        int hours = Integer.parseInt(timeElementMatcher.group(1));
        int mins  = Integer.parseInt(timeElementMatcher.group(2));
        int secs  = Integer.parseInt(timeElementMatcher.group(3));
        int fracs = Integer.parseInt(timeElementMatcher.group(4));
        
        double result = 0d;
        result += (3600d * hours);
        result += (60d * mins);
        result += secs;
        result += (0.001d * fracs);
        return result;
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length != 1) {
            throw new RuntimeException(Arrays.toString(args));
        }
        
        double offset;
        
        try {
            offset = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new RuntimeException(Arrays.toString(args), e);
        }
        
        System.err.printf("offset: '%s'\n", offset);
        
        SrtShifter shifter = new SrtShifter(System.in, System.out);
        shifter.shift(offset);
    }
    
    public static String formatTime(double seconds, int digits) {
        String timeString = Double.toString(seconds);
        
        int dotPos = timeString.indexOf(".");
        if (dotPos < 0)
            return timeString;

        Pattern p = Pattern.compile("([0-9]{1,})\\.([0-9]{1,})");
        Matcher m = p.matcher(timeString);
        if ( ! m.matches())
            throw new RuntimeException("ERROR: pattern '" + p.pattern() + "' " +
                                       "did not match input '" + timeString + "'");
        
        StringBuilder b = new StringBuilder();
        b.append(m.group(1));
        b.append('.');
        String afterCommaDigits = m.group(2);
        
        int acdLen = afterCommaDigits.length();
        if (acdLen > digits) {
            b.append(m.group(2).substring(0, digits));
        } else {
            b.append(afterCommaDigits);
            for (int i = digits - acdLen; i-- > 0; b.append('0'));
        }
        
        return b.toString();
    }
}












