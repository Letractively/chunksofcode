package com.plankenauer.fmcontrol.web.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils
{


    public static Date parse(String value) {
        String datePattern = "yyyy-MM-dd";
        try {
            return new SimpleDateFormat(datePattern).parse(value);
        } catch (Exception e) {
            throw new RuntimeException("Der Formatstring: " + datePattern
                    + " passt nicht auf den wert: " + value, e);
        }
    }

    public static String format(Date value) {
        String datePattern = "yyyy-MM-dd";
        try {
            return new SimpleDateFormat(datePattern).format(value);
        } catch (Exception e3) {
            throw new RuntimeException("Der Formatstring: " + datePattern
                    + " ist ungültig.", e3);
        }
    }
}
