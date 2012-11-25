package com.myapp.consumptionanalysis.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myapp.consumptionanalysis.chart.barchart.BarChartDataSelector;
import com.myapp.consumptionanalysis.chart.barchart.BarChartJavaScriptGenerator;
import com.myapp.consumptionanalysis.chart.barchart.BarChartQueryBuilder;
import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.config.ConfigException;
import com.myapp.consumptionanalysis.config.ConfigRepository;
import com.myapp.consumptionanalysis.config.Table;
import com.myapp.consumptionanalysis.sql.Connect;
import com.myapp.consumptionanalysis.sql.ResultSetHolder;
import com.myapp.consumptionanalysis.sql.SqlWorker;

public class ConfigTest
{

    private static final String PROJECT01 = "project01";
    private static final String ABFRAGE01 = "01-Einfach.config";
    private static final String ABFRAGE02 = "02-Zwei-Tabellen.config";
    private static final String ABFRAGE03 = "03-Jährliche-Verbrauchsummen-Mit Abstand.config";
    private static final String ABFRAGE04 = "04-Stündliche-Summierung.config";


    private static final Logger log = Logger.getLogger(ConfigTest.class);
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
    public void testAbfrage01() throws Exception {
        String abfrage = ABFRAGE01;
        Config c1 = null;
        try {
            c1 = repo.parseConfig(PROJECT01, abfrage);
        } catch (ConfigException ce) {
            handleConfigException(ce);
        }

        assertTrue(c1 != null);
        printResultAndJs(c1);
    }


    @Test
    public void testAbfrage02() throws Exception {
        String abfrage = ABFRAGE02;
        Config c1 = null;
        try {
            c1 = repo.parseConfig(PROJECT01, abfrage);
        } catch (ConfigException ce) {
            handleConfigException(ce);
        }

        assertTrue(c1 != null);
//        fetchAndPrint(c1);
        printResultAndJs(c1);
    }

    @Test
    public void testAbfrage03() throws Exception {
        String abfrage = ABFRAGE03;
        Config c1 = null;
        try {
            c1 = repo.parseConfig(PROJECT01, abfrage);
        } catch (ConfigException ce) {
            handleConfigException(ce);
        }

        assertTrue(c1 != null);
//        fetchAndPrint(c1);
        printResultAndJs(c1);
    }

    @Test
    public void testAbfrage04() throws Exception {
        String abfrage = ABFRAGE04;
        Config c1 = null;
        try {
            c1 = repo.parseConfig(PROJECT01, abfrage);
        } catch (ConfigException ce) {
            handleConfigException(ce);
        }

        assertTrue(c1 != null);
//        fetchAndPrint(c1);
        printResultAndJs(c1);
    }



    private void handleConfigException(ConfigException ce) throws ConfigException {
        log.error(ce.getErrorString(), ce);
        throw ce;
    }

    @Test
    public void testTableWordPattern() {
        List<String> hits = Table.findColumnNames("(kWh_Preis * kWh_pro15min) * 13.7603");
        String[] expected = { "kWh_Preis", "kWh_pro15min" };
        assertArrayEquals(expected, hits.toArray());
    }


    private void printResultAndJs(Config c1) throws Exception {
        BarChartDataSelector selector = new BarChartDataSelector(c1);
        ResultSetHolder dat = selector.selectData();
        System.out.println(dat);

        BarChartJavaScriptGenerator gen = new BarChartJavaScriptGenerator();

        String js = gen.generateJavaScript(c1);
        System.out.println(js);
    }

    @SuppressWarnings("unused")
    private void fetchAndPrint(Config c1) throws Exception {
        Connect connect = new Connect(c1);
        if (! connect.isConnectable()) {
            return;
        }

        BarChartQueryBuilder queryBuilder = new BarChartQueryBuilder(c1);
        final String query = queryBuilder.generateQuery();
        connect.executeWorkerWithLogging(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                Statement statement = c.createStatement();
                ResultSet result = statement.executeQuery(query);
                int columnCount = result.getMetaData().getColumnCount();
                while (result.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.getObject(i);
                    }
                }
            }
        });
    }
}
