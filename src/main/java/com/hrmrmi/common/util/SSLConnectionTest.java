package com.hrmrmi.common.util;

import com.hrmrmi.client.SSLClientConfig;
import com.hrmrmi.server.SSLConfig;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.net.Socket;

/**
 * Simple utility to verify SSL configuration is working
 */
public class SSLConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("=== SSL Configuration Test ===");
        
        // Test 1: Client SSL configuration
        System.out.println("\n1. Testing Client SSL Configuration...");
        try {
            SSLClientConfig.configure();
            System.out.println("✅ Client SSL configuration successful");
            
            // Check if truststore properties are set
            String trustStore = System.getProperty("javax.net.ssl.trustStore");
            String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
            
            if (trustStore != null && trustStorePassword != null) {
                System.out.println("✅ TrustStore configured: " + trustStore);
                System.out.println("✅ TrustStore password set");
            } else {
                System.out.println("❌ TrustStore not properly configured");
            }
        } catch (Exception e) {
            System.out.println("❌ Client SSL configuration failed: " + e.getMessage());
        }
        
        // Test 2: Server SSL configuration
        System.out.println("\n2. Testing Server SSL Configuration...");
        try {
            SslRMIServerSocketFactory serverFactory = SSLConfig.createServerFactory();
            SslRMIClientSocketFactory clientFactory = SSLConfig.createClientFactory();
            
            if (serverFactory != null && clientFactory != null) {
                System.out.println("✅ Server SSL factory created successfully");
                System.out.println("✅ Client SSL factory created successfully");
            } else {
                System.out.println("❌ SSL factories not created");
            }
        } catch (Exception e) {
            System.out.println("❌ Server SSL configuration failed: " + e.getMessage());
        }
        
        // Test 3: SSL system properties
        System.out.println("\n3. Checking SSL System Properties...");
        String keyStore = System.getProperty("javax.net.ssl.keyStore");
        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        
        if (keyStore != null) {
            System.out.println("✅ KeyStore configured: " + keyStore);
        } else {
            System.out.println("ℹ️  KeyStore not set (will be set when servers start)");
        }
        
        if (keyStorePassword != null) {
            System.out.println("✅ KeyStore password configured");
        } else {
            System.out.println("ℹ️  KeyStore password not set (will be set when servers start)");
        }
        
        System.out.println("\n=== SSL Test Complete ===");
        System.out.println("If all tests show ✅, SSL is properly configured!");
        System.out.println("\nTo test SSL connections:");
        System.out.println("1. Start the primary server first");
        System.out.println("2. Start the backup server");
        System.out.println("3. Then start the client application");
    }
}