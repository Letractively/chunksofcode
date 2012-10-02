package com.plankenauer.fmcontrol.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.plankenauer.fmcontrol.jdbc.Connect;

public class ConfigTest
{
    private static final String ABFRAGE01 = "ABFRAGE01.properties";
    private static final String ABFRAGE02 = "ABFRAGE02.properties";
    private static final String ABFRAGE03 = "ABFRAGE03.properties";
    private static final String ABFRAGE04 = "ABFRAGE04.properties";
    private static final String ABFRAGE05 = "ABFRAGE05.properties";

    private static final Logger log = Logger.getLogger(ConfigTest.class);


    @Test
    public void test2() throws IOException, ConfigException {
        log.info("ENTERING");
        String userDir = System.getProperty("user.dir");
        log.trace("working dir: " + userDir);

        File target = new File(userDir, "target");
        File testclasses = new File(target, "test-classes");
        File testConfigRepository = new File(testclasses, "testConfigRepository");

        ConfigRepository repo = new ConfigRepository(testConfigRepository);
        log.debug("testConfigRepository:" + testConfigRepository);

        try {
            repo.parseAllConfigs("project01");
            fail("error expected in " + ABFRAGE05);

        } catch (ConfigException expected) {
            String m = expected.getMessage();
            assertTrue(m, m.contains(ABFRAGE05));
            assertTrue(m,
                       m.contains("Erforderlicher Konfigurationseintrag fehlt: '"
                               + Constants.CK_CONNECTION_HOSTNAME + "'"));
        }

        Map<String, Config> allConfigs = repo.parseAllValidConfigs("project01");
        assertTrue(String.valueOf(allConfigs), allConfigs.size() == 4);

        for (Config c : allConfigs.values()) {
            assertFalse(String.valueOf(allConfigs),
                        c.getName().equalsIgnoreCase(ABFRAGE05));
        }
        for (String c : allConfigs.keySet()) {
            assertFalse(String.valueOf(allConfigs), c.equalsIgnoreCase(ABFRAGE05));
        }

        testAbfrage01(allConfigs);
        testAbfrage02(allConfigs);
        testAbfrage03(allConfigs);

        try {
            testAbfrage04(allConfigs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("EXITING");
    }


    private void testAbfrage01(Map<String, Config> allConfigs) {
        Config c1 = allConfigs.get(ABFRAGE01);
        assertTrue(c1 != null);
    }

    private void testAbfrage02(Map<String, Config> allConfigs) {
        Config c2 = allConfigs.get(ABFRAGE02);
        assertTrue(c2 != null);
    }

    private void testAbfrage03(Map<String, Config> allConfigs) {
        Config c3 = allConfigs.get(ABFRAGE03);
        assertTrue(c3 != null);
    }

    private void testAbfrage04(Map<String, Config> allConfigs) throws Exception {
        Config c4 = allConfigs.get(ABFRAGE04);
        assertTrue(c4 != null);
        Connect connect = new Connect(c4);
        connect.getAllColumns();
    }
}
