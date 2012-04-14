package com.myapp.util.soundsorter.wizard.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bff.javampd.MPD;

import com.myapp.util.format.Util;

class Config {
    
    private static Config config;


    public static Config getInstance() {
        if (config == null) {
            try {
                long start = Util.now();
                config = new Config();
                Util.log("Config.getInstance() Config read!", start);
                
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("singleton creation failed: ", e);
            }
        }
        
        return config;
    }
    
    

    public static final String nl = System.getProperty("line.separator");
    private static final String defaultConfigFile = "com.myapp.util.soundsorter.wizard.tool.DestinationDirConfig.conf";

    
    
    public static final String PROPKEY_LONESOME_FILES_DIR_NAME = "LONESOME_FILES_DIR_NAME";
    public static final String PROPKEY_MAXIMIX_DIR_NAME = "MAXIMIX_DIR_NAME";
    public static final String PROPKEY_UNSORTED_DIR_NAME = "UNSORTED_DIR_NAME";

    public static final String PROPKEY_MPD_SERVER_HOSTNAME = "MPD_SERVER_HOSTNAME";
    public static final String PROPKEY_MPD_SERVER_PORT = "MPD_SERVER_PORT";
    
    
    private InputStream configStream;
    private String configFileContent;
    private Map<String, String> variables;

    private MPD mpd;
    
    
    Config(InputStream sourceStream) throws IOException {
        if (sourceStream == null)
            throw new NullPointerException();
        
        configStream = sourceStream;
        readConfigFile();
        initVariables();
        
        String host = getProperty(Config.PROPKEY_MPD_SERVER_HOSTNAME);
        String port = getProperty(Config.PROPKEY_MPD_SERVER_PORT);

        try {
            int portNum = Integer.parseInt(port);
            mpd = new MPD(host, portNum);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Config() throws IOException {
        this(
                  Config.class
                        .getClassLoader()
                        .getResourceAsStream(defaultConfigFile)
        );
    }
    


    private void readConfigFile() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(configStream));
        StringBuilder sb = new StringBuilder();
        Matcher commentLines = Pattern.compile("^\\s*([#].*)?$").matcher("foo");
        
        for (String line = null; (line = br.readLine()) != null;) {
            if (commentLines.reset(line).matches()) {
                continue;
            }
            
            sb.append(line);
            sb.append(nl);
        }
        
        configFileContent = sb.toString();
        System.out.println("Config.readConfigFile() content='"+configFileContent.substring(0, configFileContent.indexOf("TARGET_ROOT"))+"'");
    }
    
    
    private void initVariables() {
        String[] keywords = {   
             PROPKEY_LONESOME_FILES_DIR_NAME,
             PROPKEY_MAXIMIX_DIR_NAME,
             PROPKEY_UNSORTED_DIR_NAME,
             PROPKEY_MPD_SERVER_HOSTNAME,
             PROPKEY_MPD_SERVER_PORT
        };
        
        variables = new HashMap<String, String>();
        int keywordsLength = keywords.length;
        Matcher propertyEntry = Pattern.compile("^\\s*([_A-Za-z0-9]*)\\s*[=]\\s*(.+)\\s*$").matcher("foo");
        
        for (String line : new ConfigLines()) {
            if (propertyEntry.reset(line).matches()) {
                String key = propertyEntry.group(1);
                String val = propertyEntry.group(2);

                Object prev = variables.put(key, val);

                if (prev != null) {
                    throw new IllegalStateException("duplicate variable: " + key + " first=" + prev + ", second=" + val);
                }
            }
        }
        
        for (int i = 0; i < keywordsLength; i++) {
            if ( ! variables.containsKey(keywords[i])) {
                throw new RuntimeException("missing property: " + keywords[i]);
            }
        }
    }
    
    Iterator<String> getConfigFileLineWise() {
        return new ConfigLines();
    }
    
    private class ConfigLines implements Iterable<String>, Iterator<String> {
        BufferedReader reader;
        String next;
        
        private ConfigLines() {
            reader = new BufferedReader(new StringReader(configFileContent));
            read();
        }
        
        public String next() {
            String temp = next;
            read();
            return temp;
        }
        
        private void read() {
            try {
                next = reader.readLine();
            } catch (IOException e) { // stringreader shouldn't throw
                throw new RuntimeException();
            }
        }

        public boolean hasNext() {
            return next != null;
        }

        public void remove() {
            throw new RuntimeException();
        }

        @Override
        public Iterator<String> iterator() {
            return this;
        }
    }

    public String getProperty(String key) {
        return variables.get(key);
    }
    
    public Set<String> getPropertyKeys() {
        return variables.keySet();
    }
    
    public MPD getMpd() {
        return mpd;
    }
    
}
