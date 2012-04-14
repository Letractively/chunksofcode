package com.myapp.util.log.unixcolors;

import java.io.InputStream;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public final class LogFileSelector {

    private static final Logger log = LoggerFactory.getLogger(LogFileSelector.class);

    private LogFileSelector() {}


    public static void setLogConfig(String configFileName) throws Exception {
        ILoggerFactory fac = LoggerFactory.getILoggerFactory();
        LoggerContext ctx = (LoggerContext) fac;
        System.out.println("LogFileSelector.setLogConfig() ctx received: "+ctx);
        JoranConfigurator cfg = new JoranConfigurator();
        System.out.println("LogFileSelector.setLogConfig() JoranConfigurator created: "+cfg);
        cfg.setContext(ctx);
        System.out.println("LogFileSelector.setLogConfig() context was set.");
        ctx.reset();
        System.out.println("LogFileSelector.setLogConfig() context was reset.");
        
        ClassLoader cl = LogFileSelector.class.getClassLoader();
        InputStream in = cl.getResourceAsStream(configFileName);

        try {
            cfg.doConfigure(in);
            log.debug("----------------  log config changed to {}. ----------", configFileName);

        } catch (JoranException e) {
            throw new Exception("could not change log-config to " + configFileName, e);
        }
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
}
