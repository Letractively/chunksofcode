package com.myapp.dwr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author andre
 */
public class HelloDWR {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS zzzz");
    public static final String TIME_PREFIX = "Die Serverzeit ist: '";

    public String sayHello(String name) {
        if (name == null || name.trim().length() == 0) {
            return "Sie sollten einen Namen eingeben!";
        }
        return "Hallo Benutzer namens '" + name + "'!";
    }

    public String getServerTime() {
        return TIME_PREFIX + DATE_FORMAT.format(new Date()) + "'";
    }
}
