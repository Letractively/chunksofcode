package com.myapp.util.soundsorter.wizard.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ActionExecutor implements IActionExecutor {
    
    
    private PrintWriter outputStream;
    
    
    public ActionExecutor() {
        String outputFileName = System.getProperty("user.home");
        outputFileName += System.getProperty("file.separator");
        outputFileName += "Desktop";
        outputFileName += System.getProperty("file.separator");
        outputFileName += "sortSound.sh";

        File outputFile = new File(outputFileName);
        
        try {
            outputStream = new PrintWriter(new FileOutputStream(outputFile, false));
       
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        outputStream.println("#!/bin/bash");
        outputStream.println();
        outputStream.flush();
    }
    
    
    @Override
    public int handleFiles(File interpretDir, File destinationDir) throws Exception {
        if ( ! interpretDir.exists())
            throw new Exception("interpretDir not found: " + interpretDir);
        
        if ( ! destinationDir.exists())
            throw new Exception("destinationDir not found: " + destinationDir);
        
        
        String src = interpretDir.getAbsolutePath();
        String target = destinationDir.getAbsolutePath();
        
        if ( ! target.endsWith("/")) {
            target = target + "/";
        }
        
        target += interpretDir.getName(); 
        
        if (new File(target).exists())
            throw new Exception(
                "directory "+interpretDir.getName()+" already exists in " + destinationDir.getAbsolutePath());
        
        String[] commands = {
             "mv", 
             "-v",
             src,
             target
        };
        
        String cmd = commandArrayToString(commands);
        System.out.println("executing: "+cmd);
        
        outputStream.println(cmd);
        outputStream.flush();
        
//        ProcessBuilder pb = new ProcessBuilder(commands);
//        pb.redirectErrorStream(true);
//        Process moveProcess = null;
//        
//        try {
//            moveProcess = pb.start();
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//            Util.error(interpretDir.getAbsolutePath(), e);
//            return 1;
//        }
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(moveProcess.getInputStream()));
//        StringBuilder bui = new StringBuilder();
//        
//        try {
//            for (String line = null; (line = br.readLine()) != null;) {
//                bui.append(line);
//            }
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//            Util.error(interpretDir.getAbsolutePath(), e);
//            return 1;
//        }
        
        return 0;
    }
    
    
    
    private static String commandArrayToString(String[] commands) {
        StringBuilder bui =  new StringBuilder();

        for (int i = 0; i < commands.length; i++) {
            String s = commands[i];
            
            if (s.contains(" ")) {
                bui.append("\"");
                bui.append(s);
                bui.append("\"");
            
            } else {
                bui.append(s);
            }
            
            if (i < commands.length - 1)
                bui.append(" ");
        }
        
        return bui.toString();
    }
}
