package com.plankenauer.fmcontrol.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.DriverSpy;

import org.apache.log4j.Logger;

import com.plankenauer.fmcontrol.config.Config;
import com.plankenauer.fmcontrol.config.ConnectionConfig;
import com.plankenauer.fmcontrol.config.DataSelectionConfig;

public class Connect
{
    private static final Object NL = System.getProperty("line.separator");
    private static final Logger log = Logger.getLogger(Connect.class);

    private final Config config;


    public Connect(Config config) {
        this.config = config;
    }

    public List<String> getAllColumns() throws Exception {
        try {
            DataSelectionConfig src = config.getDatasource();
            List<String> configuredSchemas = src.getSchemas();
            List<String> configuredTables = src.getTables();
            List<String> configuredColumns = src.getColumns();

            List<String> schemas;
            if (configuredSchemas == null) {
                schemas = fetchAllSchemas();
            } else {
                checkSchemas(configuredSchemas);
                schemas = configuredSchemas;
            }


            List<String> qualifiedTableNames;
            if (configuredTables == null) {
                qualifiedTableNames = fetchAllTables(schemas);
            } else {
                qualifiedTableNames = checkTables(schemas, configuredTables);
            }


            List<String> qualifiedColumnNames;
            if (configuredColumns == null) {
                qualifiedColumnNames = fetchAllColumns(qualifiedTableNames);
            } else {
                qualifiedColumnNames = checkColumns(qualifiedTableNames,
                                                    configuredColumns);
            }

            return qualifiedColumnNames;

        } finally {
            close(connection);
        }
    }

    public boolean isConnectable() {
        Exception failReason = whyNotConnectable();
        if (failReason == null) {
            return true;
        }

        log.debug("could not connect to db! "
                + failReason.toString().trim().replaceAll("\\s+", " "));
        return false;
    }

    public Exception whyNotConnectable() {
        Connection c = null;
        try {
            c = initConnection(config.getConnection(), true);
        } catch (Exception e) {
            return e;
        } finally {
            close(c);
        }
        return null;
    }

    private void checkSchemas(List<String> schemas) throws Exception {
        List<String> allSchemas = fetchAllSchemas();

        for (String schemaToCheck : schemas) {
            boolean found = false;
            for (String a : allSchemas) {
                if (a.equalsIgnoreCase(schemaToCheck)) {
                    found = true;
                    break;
                }
            }
            if (! found) {
                throw new RuntimeException("Schema " + schemaToCheck
                        + " existiert nicht in der Datenbank!");
            }
        }
    }

    /**
     * @return a list with qualified table names
     */
    private List<String>
            checkTables(List<String> schemas, List<String> tables) throws Exception {
        List<String> qualifiedTableNames = new ArrayList<>();
        List<String> allTables = fetchAllTables(schemas);

        for (String tableToCheck : tables) {
            boolean found = false;
            for (String dbtab : allTables) {
                if (dbtab.toLowerCase().endsWith(tableToCheck.toLowerCase())) {
                    found = true;
                    qualifiedTableNames.add(dbtab);
                    break;
                }
            }
            if (! found) {
                throw new RuntimeException("Tabelle " + tableToCheck
                        + " existiert nicht einer der Datenbanken " + schemas + "!");
            }
        }

        return qualifiedTableNames;
    }

    private List<String> checkColumns(List<String> qualifiedTableNames,
                                      List<String> columns) throws Exception {
        List<String> qualifiedColumnNames = new ArrayList<>();
        List<String> allColumns = fetchAllColumns(qualifiedTableNames);

        for (String toCheck : columns) {
            boolean found = false;
            for (String dbcol : allColumns) {
                if (dbcol.toLowerCase().endsWith(toCheck.toLowerCase())) {
                    found = true;
                    qualifiedColumnNames.add(dbcol);
                }
            }
            if (! found) {
                throw new RuntimeException("Spalte " + toCheck
                        + " existiert nicht in einer der Tabellen "
                        + qualifiedColumnNames + "!");
            }
        }

        return qualifiedColumnNames;
    }

    private List<String> fetchAllSchemas() throws Exception {
        final List<String> result = new ArrayList<>();

        executeWorker(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                Statement s = c.createStatement();
                ResultSet schemas = s.executeQuery("show databases");

                while (schemas.next()) {
                    String schemaName = schemas.getString(1);
                    result.add(schemaName);
                    log.trace("adding schema: " + schemaName);
                }

                schemas.close();
                s.close();
            }
        });

        log.debug(result.size() + " schemas were found.");
        return result;
    }

    private List<String> fetchAllTables(final List<String> schemas) throws Exception {
        final List<String> result = new ArrayList<>();

        final StringBuilder sb = new StringBuilder();
        sb.append("select TABLE_NAME,TABLE_SCHEMA,TABLE_TYPE ");
        sb.append(NL);
        sb.append("from INFORMATION_SCHEMA.TABLES ");
        sb.append(NL);
        sb.append("where lower(TABLE_SCHEMA) in (");
        sb.append(NL);

        Matcher sqlInjectionChecker = Pattern.compile("(?i)[_a-z0-9]+").matcher("foo");

        for (Iterator<String> i = schemas.iterator(); i.hasNext();) {
            String s = i.next();
            if (! sqlInjectionChecker.reset(s).matches()) {
                throw new RuntimeException("Kein gültiger Datenbankname: '" + s + "'");
            }
            sb.append("'");
            sb.append(s.toLowerCase());
            sb.append("'");
            if (i.hasNext()) {
                sb.append(",");
            }
            sb.append(NL);
        }
        sb.append(")");

        executeWorker(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                Statement statement = c.createStatement();
                ResultSet tables = statement.executeQuery(sb.toString());

                while (tables.next()) {
                    String tableName = tables.getString(1);
                    String tableSchema = tables.getString(2);
                    String tableType = tables.getString(3);

                    String qualifiedTableName = tableSchema + "." + tableName;
                    log.trace("adding table: " + qualifiedTableName + " (" + tableType
                            + ")");
                    result.add(qualifiedTableName);
                }

                tables.close();
                statement.close();
            }
        });

        log.debug(result.size() + " tables were found.");
        return result;
    }

    private List<String>
            fetchAllColumns(final List<String> qualifiedTableNames) throws Exception {

        final List<String> result = new ArrayList<>();
        final StringBuilder sql = new StringBuilder();

        sql.append("select c.TABLE_SCHEMA, c.TABLE_NAME, c.COLUMN_NAME, c.COLUMN_TYPE ");
        sql.append(NL);
        sql.append("from information_schema.COLUMNS c ");
        sql.append(NL);
        sql.append("where ");
        sql.append(NL);

        Matcher sqlInjectionChecker = Pattern.compile("(?i)[._a-z0-9]+").matcher("foo");

        for (Iterator<String> i = qualifiedTableNames.iterator(); i.hasNext();) {
            String qtn = i.next();

            if (! sqlInjectionChecker.reset(qtn).matches()) {
                throw new RuntimeException("Kein gültiger Tabellenname: '" + qtn + "'");
            }

            String[] split = qtn.split("\\.");
            assert split.length == 2 : "'" + qtn + "'";
            String schema = split[0];
            String table = split[1];

            sql.append("(lower(c.TABLE_NAME) = '");
            sql.append(table.toLowerCase());
            sql.append("' and lower(c.TABLE_SCHEMA) = '");
            sql.append(schema.toLowerCase());
            sql.append("')");
            sql.append(NL);

            if (i.hasNext()) {
                sql.append("or ");
            }
        }

        log.trace(sql.toString());

        executeWorker(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                PreparedStatement ps = c.prepareStatement(sql.toString());
                ResultSet columns = ps.executeQuery();

                while (columns.next()) {
                    String schema2 = columns.getString(1);
                    String table2 = columns.getString(2);
                    String column = columns.getString(3);
                    String type = columns.getString(4);

                    String qualified = schema2 + "." + table2 + "." + column;
                    log.trace("adding column: " + qualified + " (" + type + ")");
                    result.add(qualified);
                }

                columns.close();
                ps.close();
            }
        });

        log.debug(result.size() + " columns were found.");
        return result;
    }

    private Connection connection = null;

    public void executeWorker(SqlWorker worker) throws Exception {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = initConnection(config.getConnection(), false);
                }
            }
        }
        worker.run(connection);
    }

    private static Connection
            initConnection(ConnectionConfig cc, boolean connectivityTest) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = null;

        if (! connectivityTest) { // do not use log4jdbc when performing a connectivity test
            try {
                c = initLog4jdbcConnection(cc);
            } catch (Exception e) {
                log.warn("could not init log4jdbc, use plain connection instead.", e);
            }
        }

        String user = cc.getUser();
        String password = cc.getPassword();
        String hostname = cc.getHostname();
        int portnumber = cc.getPortnumber();
        String url = "jdbc:mysql://" + hostname + ":" + portnumber;

        if (c == null) {
            c = DriverManager.getConnection(url, user, password);
        }

        return c;
    }

    private static Connection
            initLog4jdbcConnection(ConnectionConfig cc) throws Exception {
        String user = cc.getUser();
        String password = cc.getPassword();
        String hostname = cc.getHostname();
        int portnumber = cc.getPortnumber();

        String url2 = "jdbc:log4jdbc:mysql://" + hostname + ":" + portnumber;
        log.debug("url: " + url2);
        DriverSpy driverSpy = new DriverSpy();

        if (driverSpy.acceptsURL(url2)) {
            log.debug("URL is accepted so we are good to move on");
            Properties connProps = new Properties();
            connProps.setProperty("user", user);
            connProps.setProperty("password", password);
            ConnectionSpy connn = (ConnectionSpy) driverSpy.connect(url2, connProps);
            log.debug("Connection established. DB version is: "
                    + driverSpy.getMajorVersion() + "." + driverSpy.getMinorVersion());

            return connn;
        }
        return null;
    }

    private static void close(Connection... connections) {
        if (connections == null || connections.length <= 0) {
            return;
        }
        for (Connection c : connections) {
            if (c == null) {
                continue;
            }
            try {
                c.close();
            } catch (Exception e) {
            }
        }
    }
}
