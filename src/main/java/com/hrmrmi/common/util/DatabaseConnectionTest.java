package com.hrmrmi.common.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Standalone utility to test database connection
 * Run with: java DatabaseConnectionTest
 */
public class DatabaseConnectionTest {
    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===");
        
        // Print configuration status
        Config.printConfigStatus();
        
        System.out.println("\nTesting database connection...");
        
        try {
            // Test database connection
            Connection conn = DBConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection successful!");
                System.out.println("   Database URL: " + Config.DB_URL);
                System.out.println("   Database User: " + Config.DB_USER);
                
                // Test a simple query
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT version()");
                if (rs.next()) {
                    System.out.println("   PostgreSQL Version: " + rs.getString(1));
                }
                
                conn.close();
                System.out.println("✅ Connection test completed successfully!");
            } else {
                System.out.println("❌ Database connection failed - connection is null or closed");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            System.out.println("   Error: " + e.getMessage());
            System.out.println("   SQL State: " + e.getSQLState());
            System.out.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== Test Complete ===");
    }
}