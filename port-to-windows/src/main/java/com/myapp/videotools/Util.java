package com.myapp.videotools;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.myapp.util.log.unixcolors.LogFileSelector;





public final class Util {
    

    public static final class DirsFirstAlphabeticFileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            if (o1 == o2) 
                return 0;
            
            boolean o1IsDir = o1.isDirectory(), o2IsDir = o2.isDirectory();
            
            if (o1IsDir && ! o2IsDir) 
                return -1;
            if (o2IsDir && ! o1IsDir) 
                return 1;
                
            return o1.getAbsolutePath().compareToIgnoreCase(o2.getAbsolutePath());
        }
    }


    
    
    public static String squeezeFileName(String fileName) {
        if (fileName.length() < 55)
            return fileName;
        
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0)
            return fileName.substring(0, 52) + "...";
        
        String suffix = fileName.substring(lastDotIndex);
        return fileName.substring(0, 52 - suffix.length()) + "..." + suffix;
    }
    
    
 


    public static void setDefaultLogLevel() {
        try {
            LogFileSelector.setLogConfig("logback-config-default.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setDebugLogLevel() {
        try {
            LogFileSelector.setLogConfig("logback-config-debug.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTraceLogLevel() {
        try {
            LogFileSelector.setLogConfig("logback-config-trace.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUnixColoredTraceLogLevel() {
        try {
            LogFileSelector.setLogConfig("logback-config-trace-unix-colored.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    
    
    public static String getOsInfoString() {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        return "Operating System: '"+osName+"', Version: '"+osVersion+"'";
    }
    


    public static String printArgs(String[] commandArray) {
        StringBuilder b = new StringBuilder();
        
        for (int i = 0; 
             i < commandArray.length; 
             b.append("\"").append(commandArray[i++]).append("\" "));
        
        return b.toString();
    }

    public static String printArgs(List<String> commandList) {
        StringBuilder b = new StringBuilder();
        
        for (Iterator<String> i = commandList.iterator();
             i.hasNext();
             b.append("\"").append(i.next()).append("\" "));
        
        return b.toString();
    }
}
