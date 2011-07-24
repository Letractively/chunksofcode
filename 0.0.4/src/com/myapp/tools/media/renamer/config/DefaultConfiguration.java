package com.myapp.tools.media.renamer.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Msg;

/**
 * this class will implement a part of the iconfigurable interface. it loads its
 * properties from a properties file inside the program jar file and provide
 * access to those methods to act as a basic default configuration.
 * 
 * this class is designed to be extended by a implementation of storing and
 * loading custom properties which will override the default properties.
 * 
 * @author andre
 */
public abstract class DefaultConfiguration implements IConfigurable,
                                                    IConstants.ISysConstants,
                                                    IConstants.IIndexConstants {

    protected static final Logger L = Log.defaultLogger();

            /* ========== constants ==========*/

    private static String APPLICATION_HOME = null;
    private static Map<String, String> DEFAULT_PROPERTIES = null;
    private static String CONFIGURATION_HOME = null;
    private static String CUSTOM_CONFIG_PATH = null;
            
    private static final String APPLICATION_HOME_DIR_NAME =  ".jRenamer";
    private static final String CONFIGURATION_DIR_NAME = "settings";
    private static final String PROPERTIES_FILENAME = "jRenamer.properties";
    private static final String PROPERTIES_JARPATH =  "/renamer.properties";

    private static boolean initialized = false;

    
    /**
     * sole default constructor with package visibility.
     */
    protected DefaultConfiguration() {}



            /* ========== new abstract methods ==========*/



    /**
     * subclasses must overridethis method to override default values.
     * 
     * @param key
     *            the key of the value which may be an custom value.
     * @return the custom string value, or null if not defined by user.
     */
    protected abstract String getCustomProperty(String key);



            /* ========== implementation ==========*/


    @Override
    public final String getString(String key) {
        if (key == null)
            return null;

        String value = getCustomProperty(key);

        if (value == null)
            value = getDefaultValue(key);

        return value;
    }

    @Override
    public String getDefaultValue(String key) {
        return getDefaultValue0(key);
    }
    
    static String getDefaultValue0(String key) {
        return defaults().get(key);
    }

    @Override
    public final int getInt(String key) throws NumberFormatException{
        return Integer.parseInt(getString(key));
    }

    /**
     * returns a set containing all keys in the default properties map.
     * 
     * @return a set containing all keys in the default properties map.
     */
    protected final Set<String> getDefaultPropertiesKeys() {
        return new HashSet<String>(defaults().keySet());
    }


    @Override
    public final boolean getBoolean(String key) {
        String val = getString(key);
        
        assert     val.equalsIgnoreCase("true") 
                || val.equalsIgnoreCase("false")
                        : "getBoolean("+key+"): "+ val;
                
        return Boolean.parseBoolean(val);
    }


    @Override
    public final String getTranslatedSystemProperty(String key) {
        final String raw = getString(key);
        Matcher m = Pattern.compile("\\{([a-z\\.]{1,})\\}").matcher(raw);

        if ( ! m.find()) return raw;

        StringBuilder result = new StringBuilder();
        int start = 0;

        do {
            result.append(raw.substring(start, m.start()));
            String property = System.getProperty(m.group(1));
            result.append(property);
            start = m.end();
        } while (m.find());

        return result.append(raw.substring(start, raw.length())).toString();
    }

    
    /**
     * replaces environment dependent strings like pathseparators or homedirs
     * with variables to keep configuration more independent.
     * 
     * @param path
     *            the string we want to make independent
     * @return the string with escaped variables<br/>
     *         e.g. c:\foo --> c:{file.separator}foo
     */
    protected static final String replaceEnvironmentVariables(String path) {
        for (String s : new String[] {"user.home", "file.separator"}) {
            String envProp = System.getProperty(s);
            while (path.contains(envProp))
                path = path.replace(envProp, "{" + s + "}");
        }
        
        return path;
    }

    /**
     * returns the default properties file from the jarfile as an inputstream
     * 
     * @return the default properties file from the jarfile as an inputstream
     * @throws IOException
     */
    protected static final InputStream defaultPropertiesStream() throws IOException {        
        return new BufferedInputStream(
                            CustomConfiguration.class
                                .getResource(PROPERTIES_JARPATH)
                                .openStream());
    }


    /**
     * returns the value of the property mapped to the given key as an integer
     * 
     * @param key
     *            the key for the property
     * @return the value of the default property number
     */
    protected static final Integer defaultInt(String key) {
        return new Integer(defaults().get(key));
    }
    
    
    
    

    private static Map<String, String> defaults() {
        checkInitialized();
        return DEFAULT_PROPERTIES;
    }
    static String getCustomConfigPath() {
        checkInitialized();
        return CUSTOM_CONFIG_PATH;
    }
    protected static String getApplicationHomePath() {
        checkInitialized();
        return CUSTOM_CONFIG_PATH;
    }
    protected static String getConfigHomePath() {
        checkInitialized();
        return CONFIGURATION_HOME;
    }
    
    private static void checkInitialized() { // wraps initDefaults, avoid syncronisation
        if ( ! initialized) {
            initDefaults();
        }
    }

    private static synchronized void initDefaults() {
        if (initialized) {
            return;
        }

        /* init DEFAULT_PROPERTIES */
        Map<String, String> m = null;

        try {
            L.info(Msg.msg("DefaultProperties.loadingDefaultProps"));
            m = PropertiesIO.read(defaultPropertiesStream());

        } catch (IOException e) {
            String msg = "error while loading properties from jar at " + PROPERTIES_JARPATH;
            L.info(Msg.msg("DefaultProperties.loadingDefaultPropsError").replace("#error#", e.toString()).replace("#path#", PROPERTIES_JARPATH));
            L.severe(msg);
            throw new RuntimeException(msg, e);
        }
        
        DEFAULT_PROPERTIES = Collections.unmodifiableMap(m);
        L.info(Msg.msg("DefaultProperties.loadingDefaultPropsDone").replace("#path#", PROPERTIES_JARPATH));

        /* init platform-dependent constant strings */
        String slash = System.getProperty("file.separator");
        String home = System.getProperty("user.home");

        APPLICATION_HOME   = home + slash + APPLICATION_HOME_DIR_NAME;
        CONFIGURATION_HOME = APPLICATION_HOME + slash + CONFIGURATION_DIR_NAME;
        CUSTOM_CONFIG_PATH = CONFIGURATION_HOME + slash + PROPERTIES_FILENAME;
        initialized = true;
    }
}
