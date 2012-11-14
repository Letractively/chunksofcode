package com.myapp.consumptionanalysis.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils
{


    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";

    public static Date parse(String value) {
        String datePattern = YYYY_MM_DD;
        try {
            return new SimpleDateFormat(datePattern).parse(value);
        } catch (Exception e) {
            throw new RuntimeException("Der Formatstring: " + datePattern
                    + " passt nicht auf den wert: " + value, e);
        }
    }

    public static String formatDate(Date value) {
        String datePattern = YYYY_MM_DD;
        try {
            return new SimpleDateFormat(datePattern).format(value);
        } catch (Exception e3) {
            throw new RuntimeException("Der Formatstring: " + datePattern
                    + " ist ungültig.", e3);
        }
    }
    
    public static String formatDayTime(Date value) {
        String datePattern = HH_MM_SS;
        try {
            return new SimpleDateFormat(datePattern).format(value);
        } catch (Exception e3) {
            throw new RuntimeException("Der Formatstring: " + datePattern
                    + " ist ungültig.", e3);
        }
    }
    
}
