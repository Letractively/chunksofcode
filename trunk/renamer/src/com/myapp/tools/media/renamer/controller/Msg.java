package com.myapp.tools.media.renamer.controller;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * encapsulates the reading of messages globally for the app.
 * 
 * @author andre
 *
 */
public class Msg {

    private static final String BUNDLE_NAME = "lang-DE";
    private static final ResourceBundle RESOURCE_BUNDLE = 
                                    ResourceBundle.getBundle(BUNDLE_NAME);

    /**
     * no instance needed.
     */
    private Msg() {}

    /**
     * reads the message from the jared properties.
     * 
     * @param key
     *            the property key
     * @return the localized message for the given key
     */
    public static String msg(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key).intern();
            
        } catch (MissingResourceException e) {
            assert false : "error: " + e;
            return '!' + key + '!';
        }
    }
}
