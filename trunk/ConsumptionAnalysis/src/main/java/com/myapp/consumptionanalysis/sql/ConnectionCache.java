package com.myapp.consumptionanalysis.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.myapp.consumptionanalysis.config.ConnectionConfig;

public final class ConnectionCache
{

    public static final class ConnectionHolder
    {
        private long connectTime;
        private Connection connection;

        public long getConnectTime() {
            return connectTime;
        }

        public Connection getConnection() {
            return connection;
        }
    }


    private static final Logger log = Logger.getLogger(ConnectionCache.class);
    private Map<String, ConnectionHolder> cache = new HashMap<>();


    private static final ConnectionCache singleton = new ConnectionCache();

    public static ConnectionCache getSingleton() {
        return singleton;
    }


    public ConnectionHolder obtain(ConnectionConfig cc) throws Exception {
        return obtain(cc.getHostname(),
                      cc.getPortnumber(),
                      cc.getUser(),
                      cc.getPassword());
    }

    public ConnectionHolder
            obtain(String hostname, int port, String user, String password) throws Exception {
        ConnectionHolder holder = null;
        String key = key(hostname, port, user);

        synchronized (cache) {
            holder = cache.get(key);
        }

        if (holder == null) {
            holder = new ConnectionHolder();
            synchronized (cache) {
                cache.put(key, holder);
            }
            log.info(key + " Init new connection...");
            establish(hostname, port, user, password, holder);
            return holder;
        }


        // we've found a hit. check if connection is still open..

        log.debug(key + " Check if still valid...");
        Connection jdbcConn = holder.connection;

        if (! isValid(key, jdbcConn)) {
            close(jdbcConn);
            log.info(key + " Reconnecting...");
            establish(hostname, port, user, password, holder);
        }

        log.info(key + " Reusing connection.");
        return holder;
    }

    private boolean isValid(String key, Connection jdbcConn) {
        boolean valid = true;

        try {
            if (jdbcConn.isClosed()) {
                valid = false;
            } else if (! jdbcConn.isValid(5)) {
                valid = false;
            } else if (jdbcConn.getMetaData().getDatabaseProductName() == null) {
                valid = false;
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn(key + " - Connection invalid.", e);
            } else {
                log.warn(key + " - Connection invalid." + e.getMessage());
            }
            valid = false;
        }
        return valid;
    }

    public void closeAll() {
        Set<Entry<String, ConnectionHolder>> entrySet = cache.entrySet();

        for (Entry<String, ConnectionHolder> entry : entrySet) {
            ConnectionHolder h = entry.getValue();
            Connection connection = h.getConnection();

            if (connection == null) {
                continue;
            }

            try {
                if (connection.isClosed()) {
                    continue;
                }
            } catch (SQLException e) {
                log.warn("Could not close connection " + entry.getKey() + "! ", e);
            }

            close(connection);
        }
    }

    private static String key(String hostname, int port, String user) {
        return (user + "@" + hostname + ":" + port).toLowerCase();
    }


    private void establish(String hostname,
                           int port,
                           String user,
                           String password,
                           ConnectionHolder result) throws Exception {
        result.connection = initConnection(user, password, hostname, port);
        result.connection.getMetaData().getDatabaseProductName();
        result.connectTime = System.currentTimeMillis();
    }

    private Connection initConnection(String user,
                                      String password,
                                      String hostname,
                                      int portnumber) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = null;

//        if (useLog4Jdbc) { // maybe do not use log4jdbc when performing a connectivity test
//        try {
//            c = initLog4jdbcConnection(user, password, hostname, portnumber);
//        } catch (Exception e) {
//            log.warn("could not init log4jdbc, use plain connection instead.", e);
//        }
//        }

        String url = "jdbc:mysql://" + hostname + ":" + portnumber;

//        if (c == null) {
            c = DriverManager.getConnection(url, user, password);
//        }

        return c;
    }

//    private static Connection initLog4jdbcConnection(String user,
//                                                     String password,
//                                                     String hostname,
//                                                     int portnumber) throws Exception {
//
//
//        String url2 = "jdbc:log4jdbc:mysql://" + hostname + ":" + portnumber;
//        log.debug("url: " + url2);
//        DriverSpy driverSpy = new DriverSpy();
//
//        if (driverSpy.acceptsURL(url2)) {
//            log.debug("URL is accepted so we are good to move on");
//            Properties connProps = new Properties();
//            connProps.setProperty("user", user);
//            connProps.setProperty("password", password);
//            ConnectionSpy connn = (ConnectionSpy) driverSpy.connect(url2, connProps);
//            log.debug("Connection established. DB version is: "
//                    + driverSpy.getMajorVersion() + "." + driverSpy.getMinorVersion());
//
//            return connn;
//        }
//        return null;
//    }

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
