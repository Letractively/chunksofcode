package com.myapp.util.log.unixcolors;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * command to remove control characters from output data:
 * 
 * cat output-file.out | sed 's/[[:cntrl:]]\[[0-9]*[;m]*[0-9]*[m\[K]*   //g' | less
 * 
 */
// cat file.out | sed 's/[[:cntrl:]]\[[0-9]*[;m]*[0-9]*[m\[K]*//g' | less
public class UnixStringColorizer {
    
    public static final Map<Color, String> COLOR_CODES;
    
    static {
        Map<Color, String> m = new HashMap<Color, String>();
        m.put(Color.black,      "01;30m");
        m.put(Color.red,        "01;31m");
        m.put(Color.green,      "01;32m");
        m.put(Color.yellow,     "01;33m");
        m.put(Color.blue,       "01;34m");
        m.put(Color.magenta,    "01;35m");
        m.put(Color.cyan,       "01;36m");
        m.put(Color.white,      "01;37m");
        COLOR_CODES = Collections.unmodifiableMap(m);
    }
    
    
    public static String paintRed(String s) {
        return colorizeString(s, Color.red);
    }
    
    public static String paintGreen(String s) {
        return colorizeString(s, Color.green);
    }
    
    public static String paintBlue(String s) {
        return colorizeString(s, Color.blue);
    }
    
    public static String paintYellow(String s) {
        return colorizeString(s, Color.yellow);
    }
    
    public static String paintMagenta(String s) {
        return colorizeString(s, Color.magenta);
    }
    
    public static String paintBlack(String s) {
        return colorizeString(s, Color.black);
    }
    
    public static String paintWhite(String s) {
        return colorizeString(s, Color.white);
    }
    
    public static String paintCyan(String s) {
        return colorizeString(s, Color.cyan);
    }
    
    private static String colorizeString(String str, Color clr) {
        String code = null;
        
        if (str == null || clr == null || (code = COLOR_CODES.get(clr)) == null) {
            assert false : clr + ", " + str + ", "+ code; // fail during developing
            return str;
        }

        return ("\033[" + code + "\033[K" + str + "\033[m\033[K");
    }    
    
    public static void main(String[] args) throws Throwable {
        System.out.println(colorizeString("black",     Color.black));
        System.out.println(colorizeString("red",       Color.red));
        System.out.println(colorizeString("green",     Color.green));
        System.out.println(colorizeString("yellow",    Color.yellow));
        System.out.println(colorizeString("blue",      Color.blue));
        System.out.println(colorizeString("magenta",   Color.magenta));
        System.out.println(colorizeString("cyan",      Color.cyan));
        System.out.println(colorizeString("white",     Color.white));
    }
}
