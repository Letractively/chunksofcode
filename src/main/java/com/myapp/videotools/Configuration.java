package com.myapp.videotools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuration {

    public static Configuration instance = null;
    public static final String propertiesFileName = "videotool.properties";
    private Properties p;
    
    public static Configuration getInstance() {
        if (instance == null) {
            synchronized (Configuration.class) {
                if (instance == null)
                    instance = new Configuration();
            }
        }

        return instance;
    }
    
    private Configuration() {
        ClassLoader sysCL = ClassLoader.getSystemClassLoader();
        InputStream propertiesStream = sysCL.getResourceAsStream(propertiesFileName);
        p = new Properties();
        
        try {
            p.load(propertiesStream);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Properties getProperties() {
        return p;
    }
    
    public String getProperty(String key) {
        return p.getProperty(key);
    }
}
