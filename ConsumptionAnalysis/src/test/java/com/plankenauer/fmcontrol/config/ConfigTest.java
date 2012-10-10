package com.plankenauer.fmcontrol.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.plankenauer.fmcontrol.jdbc.Connect;

public class ConfigTest
{
    private static final String PROJECT01 = "project01";
    private static final String ABFRAGE01 = "ABFRAGE01.properties";
    private static final String ABFRAGE02 = "ABFRAGE02.properties";
    private static final String ABFRAGE03 = "ABFRAGE03.properties";
    private static final String ABFRAGE04 = "ABFRAGE04.properties";
    private static final String ABFRAGE05 = "ABFRAGE05.properties";
    private static final String ABFRAGE20 = "ABFRAGE20.properties";

    private static final Logger log = Logger.getLogger(ConfigTest.class);
    private static final String NL = System.getProperty("line.separator");

    private static ConfigRepository repo;


    @BeforeClass
    public static void init() throws Exception {
        log.info("ENTERING");
        String userDir = System.getProperty("user.dir");
        log.trace("working dir: " + userDir);

        File target = new File(userDir, "target");
        File testclasses = new File(target, "test-classes");
        File testConfigRepository = new File(testclasses, "testConfigRepository");

        repo = new ConfigRepository(testConfigRepository);
        log.debug("testConfigRepository:" + testConfigRepository);

    }

    @Test
    public void test2() throws Exception {
        try {
            repo.parseAllConfigs(PROJECT01);
            fail("error expected in " + ABFRAGE05);

        } catch (ConfigException expected) {
            String m = expected.getMessage();
            assertTrue(m, m.contains(ABFRAGE05));
            assertTrue(m,
                       m.contains("Erforderlicher Konfigurationseintrag fehlt: '"
                               + Constants.CK_CONNECTION_HOSTNAME + "'"));
        }

        Map<String, Config> allConfigs = repo.parseAllValidConfigs(PROJECT01);
        assertTrue(String.valueOf(allConfigs), allConfigs.size() == 5);

        for (Config c : allConfigs.values()) {
            assertFalse(String.valueOf(allConfigs),
                        c.getName().equalsIgnoreCase(ABFRAGE05));
        }
        for (String c : allConfigs.keySet()) {
            assertFalse(String.valueOf(allConfigs), c.equalsIgnoreCase(ABFRAGE05));
        }

        log.info("EXITING");
    }

    @Test
    public void testAbfrage01() throws Exception {
        String abfrage = ABFRAGE01;
        Config c1 = repo.parseConfig(PROJECT01, abfrage);
        assertTrue(c1 != null);

        Connect connect = new Connect(c1);
        if (connect.isConnectable()) {
            List<String> columns = connect.getAllColumns();
            log.info("columns of " + abfrage + ": " + dumpColumnSet(columns));
        }

//      GNUPlot plot = new GNUPlot(true);
    }

    @Test
    public void testAbfrage02() throws Exception {
        String abfrage = ABFRAGE02;
        Config c2 = repo.parseConfig(PROJECT01, abfrage);
        assertTrue(c2 != null);

        Connect connect = new Connect(c2);
        if (connect.isConnectable()) {
            List<String> columns = connect.getAllColumns();
            log.info("columns of " + abfrage + ": " + dumpColumnSet(columns));
        }
    }

    @Test
    public void testAbfrage03() throws Exception {
        String abfrage = ABFRAGE03;
        Config c3 = repo.parseConfig(PROJECT01, abfrage);
        assertTrue(c3 != null);

        Connect connect = new Connect(c3);
        if (connect.isConnectable()) {
            List<String> columns = connect.getAllColumns();
            log.info("columns of " + abfrage + ": " + columns.size());
        }
    }

    @Test
    public void testAbfrage04() throws Exception {
        String abfrage = ABFRAGE04;
        Config c4 = repo.parseConfig(PROJECT01, abfrage);
        assertTrue(c4 != null);

        Connect connect = new Connect(c4);

        if (connect.isConnectable()) {
            List<String> columns = connect.getAllColumns();
            log.info("columns of " + abfrage + ": " + columns.size());
        }
    }

    @Test
    public void testAbfrage20() throws Exception {
        String abfrage = ABFRAGE20;
        Config cfg = repo.parseConfig(PROJECT01, abfrage);
        assertTrue(cfg != null);

        Connect connect = new Connect(cfg);
        if (connect.isConnectable()) {
            List<String> columns = connect.getAllColumns();
            log.info("columns of " + abfrage + ": " + columns.size());
        }
    }

    private static String dumpColumnSet(List<String> cols) {
        StringBuilder bui = new StringBuilder();
        bui.append("[");
        for (String col : cols) {
            bui.append(NL);
            bui.append(col);
        }
        bui.append(NL);
        bui.append("]");
        return bui.toString();
    }
}
