package com.myapp.tools.media.renamer.config;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

import com.myapp.tools.media.renamer.config.Config;
import com.myapp.tools.media.renamer.config.IConstants;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Main;
import com.myapp.tools.media.renamer.model.naming.impl.NummerierungNamePart;


@ SuppressWarnings("serial")
public class ConfigurationTest implements
                              IConstants.IIndexConstants,
                              IConstants.INameConstants,
                              IConstants.ISysConstants {

    private static final boolean verbose = false;
    private static final Logger L = Log.defaultLogger();

    static { // suppress log output
        Config.L.setFilter(new Filter() {
            public boolean isLoggable(LogRecord record) {
                return false;
            }
        });
    }
    
    private IRenamerConfiguration cfg;

    public ConfigurationTest() {
        cfg = Config.getInstance();
        cfg.clearCustomProperties();
    }


    private static final Properties TEST_PROPERTIES = new Properties() {{
        setProperty("NUMMERIERUNG_PREFIX", "");
        setProperty("ORIGINALNAME_SUFFIX", ")");
        setProperty("NUMMERIERUNG_START", "1010");
        setProperty("INDEX_NUMMERIERUNG", "1");
        setProperty("ORIGINALNAME_MIT_ENDUNG", "true");
        setProperty("INDEX_DATUM", "0");
        setProperty("PREFIX", "ragg ");
        setProperty("BESCHREIBUNG_PREFIX", "");
        setProperty("DEFAULT_NAME_TITEL", "titel");
        setProperty("INDEX_THEMA", "2");
        setProperty("NUMMERIERUNG_SUFFIX", "A - ");
        setProperty("TITEL_PREFIX", "");
        setProperty("NUMMERIERUNG_ANSTIEG", "1");
        setProperty("DATUM_PREFIX", "");
        setProperty("THEMA_PREFIX", "");
        setProperty("DATUM_FORMAT", "yyyy-MM-dd");
        setProperty("SUFFIX", "");
        setProperty("BESCHREIBUNG_SUFFIX", "");
        setProperty("REPLACE_ORIGINAL_FILES", "false");
        setProperty("ORIGINALNAME_PREFIX", "(orig:");
        setProperty("DESTINATION_RENAMED_FILES", 
                      "{user.home}{file.separator}Desktop{file.separator}temp");
        setProperty("DEFAULT_NAME_BESCHREIBUNG", "beschreibung");
        setProperty("DEFAULT_NAME_THEMA", "thema");
        setProperty("INDEX_TITEL", "3");
        setProperty("TITEL_SUFFIX", " - ");
        setProperty("INDEX_ORIGINALNAME", "-1");
        setProperty("DATUM_SUFFIX", " - ");
        setProperty("THEMA_SUFFIX", " - ");
        setProperty("INDEX_BESCHREIBUNG", "4");
        if (verbose) L.info("hardcoded test values initialized.");
    }};

    @Test
    public void testGetString () {
        Enumeration<Object> e;
        for (e = TEST_PROPERTIES.keys(); e.hasMoreElements();) {
            Object hcKeyObject = e.nextElement();

            assertNotNull(hcKeyObject);
            assertNotNull(TEST_PROPERTIES.get(hcKeyObject));

            String hcKey = (String)hcKeyObject;
            assertNotNull(cfg.getString(hcKey));
            assertEquals("key = " + hcKeyObject,
                         TEST_PROPERTIES.get(hcKey), cfg.getString(hcKey));
        }
        if (verbose) L.info("--OK--");
    }

    @Test
    public void testGetInt () {
        assertEquals(1010,  cfg.getInt(NUMMERIERUNG_START));
        assertEquals(1,     cfg.getInt(NUMMERIERUNG_ANSTIEG));
        assertEquals(3,     cfg.getInt(INDEX_TITEL));
        assertEquals(-1,    cfg.getInt(INDEX_ORIGINALNAME));
        assertEquals(4,     cfg.getInt(INDEX_BESCHREIBUNG));
        assertEquals(1,     cfg.getInt(INDEX_NUMMERIERUNG));
        assertEquals(2,     cfg.getInt(INDEX_THEMA));
        if (verbose) L.info("--OK--");
    }

    @Test
    public void testGetBoolean () {
        assertTrue(cfg.getBoolean(ORIGINALNAME_MIT_ENDUNG));
        assertFalse(cfg.getBoolean(REPLACE_ORIGINAL_FILES));
        if (verbose) L.info("--OK--");
    }

    @Test
    public void testGetTranslatedSystemProperty () {
        assertEquals(cfg.getTranslatedSystemProperty(DESTINATION_RENAMED_FILES), 
                     new StringBuilder()
                            .append(System.getProperty("user.home"))
                            .append(System.getProperty("file.separator"))
                            .append("Desktop")
                            .append(System.getProperty("file.separator"))
                            .append("temp")
                            .toString());
        if (verbose) L.info("--OK--");
    }

    @Test
    public void testSetCustomNameElement () {       
        cfg.setCustomNameElement(NummerierungNamePart.class, 777);
        assertEquals((Object)777, 
                     (Object)cfg.getIndex(NummerierungNamePart.class));
        if (verbose) L.info("--OK--");
    }

    @Test
    public void testResetUserDefinedNameElements () {
        cfg.setCustomNameElement(NummerierungNamePart.class, 888);

        assertEquals((Object)888,
                     (Object)cfg.getIndex(NummerierungNamePart.class));
        assertEquals(888, cfg.getIndexNummerierung());

        cfg.clearCustomProperties();
        assertEquals((Object)1,
                     (Object)cfg.getIndex(NummerierungNamePart.class));
        if (verbose) L.info("--OK--");
    }   

    @Test
    public void testReflectPublicGetters() {        
        StringBuilder bui = new StringBuilder("public getters:");
        Method[] methods = IRenamerConfiguration.class.getMethods();
        Object[] params = new Object[0];

        bui.append(System.getProperty("line.separator"));
        bui.append(System.getProperty("line.separator"));

        for (Method m : methods) {
            if ( ! m.getName().startsWith("get")      ) continue;
            if ( ! Modifier.isPublic(m.getModifiers())) continue;
            if ( m.getParameterTypes().length != 0    ) continue;

            try {
                Object returnVal = m.invoke(Config.getInstance(), params);
                String name = m.getName();
                bui.append(name);
                bui.append("()");

                for (int i = name.length(); i < 25; i++)
                    bui.append(' ');

                bui.append(" -> '" + returnVal + "'");
            } catch (Exception e) {
                L.throwing(Main.class.getName(), "main", e);
                throw new RuntimeException(e);
            }
        }
        L.finest(bui.toString());
        if (verbose) L.info("--OK--");
    }
}
