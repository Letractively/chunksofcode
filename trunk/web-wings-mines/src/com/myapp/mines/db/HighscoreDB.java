package com.myapp.mines.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class HighscoreDB {
    
    private static final Object CLASS_LOCK = new Object();
    private static HighscoreDB instance = null;
    
    public static HighscoreDB getInstance() {
        if (instance == null) {
            synchronized (CLASS_LOCK) {
                if (instance == null) {
                    instance = new HighscoreDB();
                }
            }
        }
        return instance;
    }



    private Boolean connectionOK = null; // null indicates: not yet determined
    
    
    private HighscoreDB() {}
    
    
    public boolean isDbAvailable() {
        if (connectionOK != null) {
            return connectionOK.booleanValue();
        }
        
        connectionOK = Boolean.FALSE;
        Connection c;
        
        try {
            c = getConnection();
        } catch (Exception e) {
            System.err.println("Disabling db highscore list, because of: "+e);
            return false;
        }

        try {
            c.close();
            connectionOK = Boolean.TRUE;
            System.err.println("Enabling db highscore list.");
        } catch (SQLException e) {
        }
        
        return connectionOK;
    }

    
    public void addHighscoreEntry(HighscoreEntry entry) {
        if (! isDbAvailable()) {
            return;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            conn.setReadOnly(false);
            stmt = conn.prepareStatement( 
                 "INSERT INTO tminesweeperhighscore (player_name, game_millisecs) VALUES (?, ?)"
             );
             stmt.setString(1, entry.getName());
             stmt.setLong(2, entry.getGameTime());
             
            synchronized (CLASS_LOCK) {
                stmt.execute();
                conn.commit();
            }

            System.out.println("HighscoreDB.addHighscoreEntry() hiscore added");
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    public List<HighscoreEntry> getHighscoreEntries() {
        if (! isDbAvailable()) {
            return null;
        }
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            conn.setReadOnly(true);
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(
                "select player_name, game_millisecs " +
            	"from tminesweeperhighscore " +
            	"order by game_millisecs asc " +
            	"fetch first 10 rows only"
            );
            List<HighscoreEntry> scores = new ArrayList<HighscoreEntry>();
            while (result.next()) {
                HighscoreEntry entry = new HighscoreEntry(
                    result.getString(1), result.getLong(2)
                );
                scores.add(entry);
            }
            return scores;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    
    private Connection getConnection() throws NamingException, SQLException {
        /* to configure this connection, you need to do following:

        add this to web.xml of this project:
        <resource-ref>
            <description>postgreSQL Datasource example</description>
            <res-ref-name>jdbc/postgres-andre1</res-ref-name>
            <res-type>javax.sql.DataSource</res-type>
            <res-auth>Container</res-auth>
        </resource-ref>
    
        add this to tomcat_base/conf/context.xml (within <Context> tag)
        <Resource name="jdbc/postgres-andre1"
                 auth="Container"
                 type="javax.sql.DataSource"
                 maxActive="100" 
                 maxIdle="30"
                 maxWait="10000"
                 username="andre"
                 password="*********"
                 driverClassName="org.postgresql.Driver"
                 url="jdbc:postgresql://localhost:5432/andre1" />
         */
        
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/postgres-andre1" );
        Connection connection = ds.getConnection();
        return connection;
    }
}
