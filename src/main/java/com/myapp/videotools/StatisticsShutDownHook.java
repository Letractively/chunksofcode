package com.myapp.videotools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StatisticsShutDownHook extends Thread {

    static final String NL = System.getProperty("line.separator");
    static final Logger log = LoggerFactory.getLogger(StatisticsShutDownHook.class);

    public void run() {
        currentThread().setName("summary");
        AppStatistics s = AppStatistics.getInstance();
        s.setApplicationExit();
        log.info(NL + s.toString());
    }
}