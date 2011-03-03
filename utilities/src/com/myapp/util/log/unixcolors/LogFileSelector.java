package com.myapp.util.log.unixcolors;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LogFileSelector {


    private static final Logger log = LoggerFactory.getLogger(LogFileSelector.class);



    public static void setLogConfig(String configFileName) throws Exception {
        LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator cfg = new JoranConfigurator();
        cfg.setContext(ctx);
        ctx.reset();

        ClassLoader cl = LogFileSelector.class.getClassLoader();
        InputStream in = cl.getResourceAsStream(configFileName);

        try {
            cfg.doConfigure(in);
            log.debug("----------------  log config changed to {}. ----------", configFileName);

        } catch (JoranException e) {
            throw new Exception("could not change log-config to " + configFileName, e);
        }
    }
}
