package com.hrmrmi.client;

public class SSLClientConfig {
    private SSLClientConfig() {
        // Prevent instantiation
    }

    /**
     * Configure SSL properties for RMI client.
     * Call once before any RMI lookup when SSL is enabled on server.
     */
    public static void configure() {
        System.setProperty(
                "javax.net.ssl.trustStore",
                "src/main/resources/security/client.truststore"
        );
        System.setProperty(
                "javax.net.ssl.trustStorePassword",
                "changeit"
        );
    }
}
