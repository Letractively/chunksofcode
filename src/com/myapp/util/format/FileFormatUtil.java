package com.myapp.util.format;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFormatUtil {

    private static final Pattern  HUMAN_READABLE_PATTERN = Pattern.compile("[0-9]{0,3}([.,][0-9]{0,2})?");
    private static final String[] ISO_SIZE_PREFIXES      = new String[] {"K","M","G","T","P","E","Z","Y"};

    
    public static String getHumanReadableFileSize(File f) {
        return getHumanReadableFileSize(f.length());
    }
    
    public static String getHumanReadableFileSize(long lengthInBytes) {
        double len = new Long(lengthInBytes).doubleValue();
        String suffix = "";
        
        for (int i = 0; i < ISO_SIZE_PREFIXES.length; i++) {
            if (len < 1000) break;
            
            len = len / 1000;
            suffix = ISO_SIZE_PREFIXES[i];
        }
        
        String size = Double.toString(len);
        Matcher m =  HUMAN_READABLE_PATTERN.matcher(size);
        
        if (m.find()) {
            size = m.group();
            
        } else {
            throw new RuntimeException(size);
        }
        
        size = size.trim();
        size = size.substring(0, size.length() >= 4 ? 4 : size.length());
        
        if (size.contains(".")) {
            while (size.endsWith("0")) {
                size = size.substring(0, size.length() - 1);
            }
        }
        
        if (size.endsWith(".")) size = size.substring(0, size.length() - 1);
        
        return size + " " + suffix + "B";
    }
}
