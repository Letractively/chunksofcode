package com.myapp.videotools.impl;

import static com.myapp.videotools.cli.Parameters.FLAG_DEBUG_OUTPUT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.myapp.util.log.unixcolors.LogFileSelector;
import com.myapp.videotools.Util;
import com.myapp.videotools.cli.CommandLineInterface;

@SuppressWarnings("unused")
public class RunMain {
    
    public static void main(String[] foo) {
//        LogFileSelector.setUnixColoredTraceLogLevel();
        LogFileSelector.setTraceLogLevel();
//        LogFileSelector.setDebugLogLevel();
//        LogFileSelector.setDefaultLogLevel();
        Logger logger = LoggerFactory.getLogger(RunMain.class);
        Marker marker = MarkerFactory.getMarker("myMarker");

        System.out.println("RunMain.main() marker:            "+marker);
        System.out.println("RunMain.main() marker.getClass(): "+marker.getClass());
        
        logger.trace(marker, "hallo");
        
        
//        try {
//            String[] args = {
//                "-bigpic",
//                "-R",
//                "-vr",
//                "/media/datadisk/porn/videos/to_sort",
//                "--trace"
//            };
//            
//            CommandLineInterface.main(args);
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
