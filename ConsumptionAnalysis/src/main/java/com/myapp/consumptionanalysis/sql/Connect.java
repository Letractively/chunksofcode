package com.myapp.consumptionanalysis.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.log4jdbc.ConnectionSpy;

import org.apache.log4j.Logger;

import com.myapp.consumptionanalysis.config.Config;

public class Connect implements Serializable
{
    private static final long serialVersionUID = - 7688925558037396238L;
    
    private static final Object NL = System.getProperty("line.separator");
    private static final Logger log = Logger.getLogger(Connect.class);

    private final Config config;


    public Connect(Config config) {
        this.config = config;
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
        try {
            ConnectionCache.getSingleton().obtain(config.getConnection()).getConnection();
        } catch (Exception e) {
            return e;
        }
        return null;
    }

    public List<String> fetchAllSchemas() throws Exception {
        final List<String> result = new ArrayList<>();

        executeWorkerWithoutLogging(new SqlWorker() {
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

    public List<String> fetchAllTables(final List<String> schemas) throws Exception {
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

        executeWorkerWithoutLogging(new SqlWorker() {
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


    public static final class DBCol implements Serializable
    {
        private static final long serialVersionUID = 2809174772068887114L;
        
        private final String schema;
        private final String table;
        private final String colname;
        private final String type;

        public DBCol(String schema, String table, String colname, String type) {
            super();
            this.schema = schema;
            this.table = table;
            this.colname = colname;
            this.type = type;
        }

        public String getSchema() {
            return schema;
        }

        public String getTable() {
            return table;
        }

        public String getColname() {
            return colname;
        }

        public String getType() {
            return type;
        }

        public String getQualifiedName() {
            return schema + "." + table + "." + colname;
        }
        
        public String toString() {
            return "DBCol["+table + "." + colname+" "+getType()+"]";
        }
    }

    public List<String>
            fetchAllColumns(final List<String> qualifiedTableNames) throws Exception {
        final List<String> result = new ArrayList<>();

        List<DBCol> detailed = fetchAllColumnsDetailed(qualifiedTableNames);
        for (DBCol dbCol : detailed) {
            result.add(dbCol.getQualifiedName());
        }

        return result;
    }

    public List<DBCol>
            fetchAllColumnsDetailed(List<String> qualifiedTableNames) throws Exception {
        final List<DBCol> result = new ArrayList<>();
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

        executeWorkerWithoutLogging(new SqlWorker() {
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
                    result.add(new DBCol(schema2, table2, column, type));
                }

                columns.close();
                ps.close();
            }
        });

        log.debug(result.size() + " columns were found.");
        return result;
    }

//    private static final ConnectionCache cache = new ConnectionCache();
    private Connection connection = null;

    public void executeWorkerWithLogging(SqlWorker worker) throws Exception {
        executeWorkerImpl(worker, true);
    }

    public void executeWorkerWithoutLogging(SqlWorker worker) throws Exception {
        executeWorkerImpl(worker, false);
    }
 
    private void
            executeWorkerImpl(SqlWorker worker, boolean withLog4Jdbc) throws Exception {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = ConnectionCache.getSingleton()
                                                .obtain(config.getConnection())
                                                .getConnection();
                }
            }
        }
        Connection workWith = connection;
        if (withLog4Jdbc) {
            ConnectionSpy spy = new ConnectionSpy(connection);
            workWith = spy;
        }
        worker.run(workWith);
    }

}
