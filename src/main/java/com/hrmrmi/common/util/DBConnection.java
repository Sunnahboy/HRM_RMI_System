
/*
 * DBConnection encapsulates database connectivity for the HRM application.
 * It abstracts JDBC connection creation using configuration parameters and
 * allows controlled use of a shared connection to support high-availability
 * and replication scenarios.
 */


package com.hrmrmi.common.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection sharedConnection;
    private static final Object lock = new Object();

    // This method establishes a connection to the PostgreSQL database
    public static Connection getConnection() throws SQLException {
        // If there's a shared connection (for backup server), use it
        synchronized (lock) {
            if (sharedConnection != null) {
                return sharedConnection;
            }
        }
        
        // Otherwise create a new connection (for primary server)
        try {
            // 1. Loa PostgreSQL Driver
            Class.forName(Config.DB_DRIVER);

            // 2. Open the connection using Config file
            return DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);

        } catch (ClassNotFoundException e) {
            //PostgreSQL library is missing from project
            throw new SQLException("PostgreSQL Driver not found. Did you add the dependency?", e);
        }
    }

    // Set a shared connection (used by backup server when becoming active)
    public static void setSharedConnection(Connection connection) {
        synchronized (lock) {
            sharedConnection = connection;
        }
    }

    // Clear the shared connection
    public static void clearSharedConnection() {
        synchronized (lock) {
            sharedConnection = null;
        }
    }
}