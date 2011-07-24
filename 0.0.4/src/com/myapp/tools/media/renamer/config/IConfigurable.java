package com.myapp.tools.media.renamer.config;

import java.io.IOException;
import java.io.InputStream;

/**
 * definition of methods basically needed to store and load properties for an
 * application.
 * 
 * @author andre
 */
public interface IConfigurable {

    /**
     * sets a custom property value to a specific key
     * 
     * @param key
     *            the key of the value to be set
     * @param value
     *            the new value for this property
     */
    void setCustomProperty(String key, String value);

    /**
     * empties all custom properties, and returns to default properties.
     */
    void clearCustomProperties();

    /**
     * persists the custom properties
     * 
     * @return true if the operation was successful
     * @throws IOException
     */
    boolean saveCustomSettings() throws IOException;

    /**
     * loads the custom properties.
     * 
     * @return true if the operation was successful
     * @throws IOException
     */
    boolean loadCustomSettings() throws IOException;

    /**
     * returns the value of the property mapped to the given key
     * 
     * @param key
     *            the key value for the property
     * @return the value for the keys property
     */
    String getString(String key);
    
    /**
     * returns the DEFAULT value of the property mapped to the given key
     * 
     * @param key
     *            the key value for the property
     * @return the DEFAULT value for the keys property
     */
    String getDefaultValue(String key);

    /**
     * returns the value of the property mapped to the given key parsed to an
     * int value
     * 
     * @param key
     *            the key value for the property
     * @return the value for the keys property
     * @throws NumberFormatException
     *             if key non-existing
     */
    int getInt(String key) throws NumberFormatException;

    /**
     * returns the value of the property mapped to the given key parsed to an
     * int boolean
     * 
     * @param key
     *            the key value for the property
     * @return the value for the keys property
     */
    boolean getBoolean(String key);

    /**
     * returns the value of the property mapped to the given key parsed to an
     * system property: a string like {user.home}{file.separator}foo will be
     * translated to /home/you/foo and so on
     * 
     * @param key
     *            the key value for the property
     * @return the translated value for the keys property
     */
    String getTranslatedSystemProperty(String key);

    /**
     * returns the content of the currently used configuration. if no custom
     * configuration file is existent, the default config is being read.
     * 
     * @return the custom configuration, or, if not existing, the default config
     */
    InputStream getAsStream();
}
