
package com.hrmrmi.common.util;
import java.sql.Connection;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * Config centralizes configuration management for the HRM system.
 * It loads settings from application properties, optional environment files,
 * and system environment variables, providing a unified and override-safe
 * mechanism for database, RMI, and SSL configuration.
 */

public class Config {
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        
        // Load from application.properties first
        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Could not load application.properties: " + e.getMessage());
        }
        
        // Load from .env file (for local development)
        loadEnvFile();
    }
    
    private static void loadEnvFile() {
        try (FileInputStream fis = new FileInputStream(".env")) {
            Properties envProps = new Properties();
            envProps.load(fis);
            
            // Add .env properties to main properties
            for (String key : envProps.stringPropertyNames()) {
                String value = envProps.getProperty(key);
                if (value != null && !value.trim().isEmpty()) {
                    properties.setProperty(key, value.trim());
                    // Also set as system property for other libraries that might need it
                    System.setProperty(key, value.trim());
                }
            }
            System.out.println("Loaded environment variables from .env file");
        } catch (IOException e) {
            // .env file is optional
            System.out.println("No .env file found (this is normal for production)");
        }
    }
    
    private static String getProperty(String key, String defaultValue) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.trim().isEmpty()) {
            System.out.println("Using environment variable: " + key + " = [PROTECTED]");
            return envValue.trim();
        }
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.trim().isEmpty()) {
            System.out.println("Using config property: " + key + " = [PROTECTED]");
            return propValue.trim();
        }
        System.out.println("Using default value: " + key + " = " + defaultValue);
        return defaultValue;
    }
    
    /**
     * Debug method to print configuration status
     */
    public static void printConfigStatus() {
        System.out.println("=== Database Configuration Status ===");
        System.out.println("DB_USER: " + (DB_USER != null ? "[SET]" : "[NULL]"));
        System.out.println("DB_PASSWORD: " + (DB_PASSWORD != null ? "[SET]" : "[NULL]"));
        System.out.println("DB_PASSWORD length: " + (DB_PASSWORD != null ? DB_PASSWORD.length() : 0));
        System.out.println("HRM_SERVER_IP: " + RMI_HOST);
        System.out.println("=====================================");
    }

    // Backup RMI configuration
    public static final int BACKUP_RMI_PORT = 54322;
    public static final String BACKUP_RMI_NAME = "HRMServiceBackup";

    public static final String DB_URL = "jdbc:postgresql://localhost:5432/HRM_Service";

    public static final String DB_USER = getProperty("MY_DB_USER", "postgres");

    public static final String DB_PASSWORD = getProperty("MY_DB_PASS", "password");

    public static final String DB_DRIVER = "org.postgresql.Driver";

    public static final int RMI_PORT = 54321;
    public static final String RMI_NAME = "HRMService";

    public static final String RMI_HOST = getProperty("RMI_HOST", "localhost");

    // SSL placeholders (future-ready)
    public static final String KEYSTORE_PATH = "src/main/resources/security/server.keystore";

    public static final String TRUSTSTORE_PATH = "src/main/resources/security/client.truststore";

    public static void testDatabaseConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            if (!conn.isValid(2)) {
                throw new RuntimeException("Database connection invalid");
            }
        } catch (Exception e) {
            throw new RuntimeException("Database unavailable", e);
        }
    }
}

