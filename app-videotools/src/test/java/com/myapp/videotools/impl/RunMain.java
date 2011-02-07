package com.myapp.videotools.impl;

import com.myapp.videotools.cli.CommandLineInterface;

public class RunMain {
    public static void main(String[] foo) {
        try {
            String[] args = {
                "-bigpic",
                "-R",
                "-vr",
                "/media/datadisk/porn/videos/to_sort",
                "--trace"
            };
            
            CommandLineInterface.main(args);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
