package com.hrmrmi.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // This method establishes a connection to the PostgreSQL database
    public static Connection getConnection() throws SQLException {
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
}