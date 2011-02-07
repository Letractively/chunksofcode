package com.myapp.util.timedate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author andre
 */
public class TimeDateUtil {

    private static final long MILLIS = 1000;
    private static final long SECONDS = 60;
    private static final long MINUTES = 60;
    private static final long HOURS = 24;
    private static final long DAYS = 365;
    private static final long YEARS = Long.MAX_VALUE;
    private static final long[] ALL_NUMBERS = new long[]{MILLIS, SECONDS, MINUTES, HOURS, DAYS, YEARS};
    private static final String[] ALL_PREFIXES = new String[]{".", " ", " ", " ", " ", ""};
    private static final String[] ALL_SUFFIXES = new String[]{"s", "", "m", "h", "days", "years"};
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.S");

    public static double secondsDouble(long durationLong) {
        return (Math.round((double) durationLong) / 1000.0);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static String formatTime(long durationLong) {
        StringBuilder bui = new StringBuilder();

        for (int i = 0; i < ALL_NUMBERS.length; i++) {

            /*always return at least millis and seconds*/
            if (i >= 2 && durationLong == 0)
                break;

            bui.insert(0, ALL_SUFFIXES[i]);
            bui.insert(0, durationLong % ALL_NUMBERS[i]);
            bui.insert(0, ALL_PREFIXES[i]);

            durationLong /= ALL_NUMBERS[i];
        }

        bui.trimToSize();
        return bui.toString();
    }

    public static String formatDate(long date) {
        return DATE_FORMAT.format(date);
    }

}
