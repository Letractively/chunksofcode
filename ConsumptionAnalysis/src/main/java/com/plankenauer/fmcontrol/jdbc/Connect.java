package com.plankenauer.fmcontrol.jdbc;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.plankenauer.fmcontrol.config.Config;
import com.plankenauer.fmcontrol.config.ConnectionConfig;
import com.plankenauer.fmcontrol.config.DataSelectionConfig;

public class Connect
{
    private final Logger log = Logger.getLogger(Connect.class);

    private final Config config;


    public Connect(Config config) {
        this.config = config;
    }

    public List<String> getAllColumns() throws Exception {
        DataSelectionConfig src = config.getDatasource();

        
        List<String> schemas = src.getSchemas();
        if (schemas == null) {
            schemas = fetchAllSchemas();
        } else {
            checkSchemas(schemas);
        }

        
        List<String> tables = src.getTables();
        if (tables == null) {
            tables = fetchAllTables(schemas);
        } else {
            checkTables(schemas, tables);
        }

        
        List<String> columns = src.getColumns();
        if (columns == null) {
            columns = fetchAllColumns(tables);
        } else {
            checkColumns(schemas, tables, columns);
        }

        return columns;
    }

    public boolean isConnectable() {
        try {
            initConnection();
            return true;

        } catch (Exception e) {
            log.error("could not connect to db!", e);
            return false;
        }
    }

    private Connection initConnection() throws SQLException, Exception {
        Class.forName("com.mysql.jdbc.Driver");
//        connection = DriverManager.getConnection("jdbc:mysql://hostname:port/dbname",
//                                                 "username",
//                                                 "password");
//        connection.close();

        ConnectionConfig cc = config.getConnection();
        String firstDatabase = config.getDatasource().getSchemas().get(0);

        String url = "jdbc:mysql://" + cc.getHostname() + ":" + cc.getPortnumber()
//                +"/"+firstDatabase
        ;

        String user = cc.getUser();
        String password = cc.getPassword();

        Connection connection2 = DriverManager.getConnection(url, user, password);
        return connection2;
    }

    private void checkSchemas(List<String> schemas) {
//        config.getConnection().

    }

    private void checkTables(List<String> schemas, List<String> tables) {
        // TODO Auto-generated method stub

    }

    private void checkColumns(List<String> schemas,
                              List<String> tables,
                              List<String> columns) {
        // TODO Auto-generated method stub

    }

    private List<String>
            fetchAllColumns(final List<String> qualifiedTableNames) throws Exception {
        final List<String> result = new ArrayList<>();

        executeWorker(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                DatabaseMetaData meta = c.getMetaData();
                
                for (String tableName : qualifiedTableNames) {
                    String[] split = tableName.split(".");
                    assert split.length == 2 : "'" + tableName + "'";
                    String schema = split[0];
                    String table = split[1];

                    ResultSet columns = meta.getColumns(null, schema, table, null);
                    while (columns.next()) {
                        String schema2 = columns.getString(2);
                        String table2 = columns.getString(3);
                        String column = columns.getString(4);
                        
                        String qualified = schema2+"."+table2+"."+column;
                        log.debug("adding column: " + qualified);
                        result.add(qualified);
                    }
                }
            }
        });

        return result;
    }

    private List<String> fetchAllTables(final List<String> schemas) throws Exception {
        final List<String> result = new ArrayList<>();

        executeWorker(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                DatabaseMetaData metaData = c.getMetaData();

                for (String schema : schemas) {
                    ResultSet tables = metaData.getTables(null, schema, null, null);

                    while (tables.next()) {
                        String schemaName = tables.getString(2);
                        String tableName = tables.getString(3);
                        
                        String qualifiedTableName = schemaName + "." + tableName;
                        log.debug("adding table: " + qualifiedTableName);
                        result.add(qualifiedTableName);
                    }
                }
            }
        });

        return result;
    }

    private List<String> fetchAllSchemas() throws Exception {
        final List<String> result = new ArrayList<>();

        executeWorker(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                DatabaseMetaData metaData = c.getMetaData();
                ResultSet schemas = metaData.getSchemas();

                while (schemas.next()) {
                    String schemaName = schemas.getString(1);
                    result.add(schemaName);
                    log.debug("adding schema: " + schemaName);
                }
            }
        });

        return result;
    }


    private void executeWorker(SqlWorker worker) throws Exception {
        Connection c = null;
        try {
            c = initConnection(); // TODO: pooling
            worker.run(c);
        } finally {
            DB.close(c);
        }
    }
}

interface SqlWorker
{
    public void run(Connection c) throws Exception;
}

final class DB
{
    static void close(Connection... connections) {
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
