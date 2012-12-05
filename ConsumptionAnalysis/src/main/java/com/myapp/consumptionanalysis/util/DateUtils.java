package com.myapp.consumptionanalysis.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.myapp.consumptionanalysis.config.Constants;

public final class DateUtils
{

    private DateUtils() {
    }


    public static Date normalizeDayTimeDate(Date time) {
        if (time == null) {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);
        
        cal.setTimeInMillis(0L);
        
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);
        cal.set(Calendar.SECOND, ss);
        
        return cal.getTime();
    }

    public static Date normalizeDateBoundDate(Date time) {
        if (time == null) {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        cal.setTimeInMillis(0L);
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        
        return cal.getTime();
    }

    public static String daytimeToString(Date date) {
        if (date == null) {
            return null;
        }
        String s = new SimpleDateFormat(Constants.HH_MM_SS).format(date);
        return s;
    }

    public static String dayToString(Date date) {
        if (date == null) {
            return null;
        }
        String s = new SimpleDateFormat(Constants.YYYY_MM_DD).format(date);
        return s;
    }

//    public static Calendar newEmptyCalendar() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(0L);
//        return cal;
//    }

//    public static Date parse(String value) {
//        String datePattern = Constants.YYYY_MM_DD;
//        try {
//            return new SimpleDateFormat(datePattern).parse(value);
//        } catch (Exception e) {
//            throw new RuntimeException("Das Format: " + datePattern
//                    + " passt nicht auf den Wert: " + value, e);
//        }
//    }

    public static String formatDate(Date value) {
        String datePattern = Constants.YYYY_MM_DD;
        try {
            return new SimpleDateFormat(datePattern).format(value);
        } catch (Exception e3) {
            throw new RuntimeException("Das Format: " + datePattern
                    + " ist ungültig.", e3);
        }
    }

//    public static Date val2date(Long val) {
//        if (val == null) {
//            return null;
//        }
//        Date d = new Date(val);
//        return d;
//    }

    public static String formatDayTime(Date value) {
        String datePattern = Constants.HH_MM_SS;
        try {
            return new SimpleDateFormat(datePattern).format(value);
        } catch (Exception e3) {
            throw new RuntimeException("Das Format: " + datePattern
                    + " ist ungültig.", e3);
        }
    }

}
