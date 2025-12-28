package com.hrmrmi.client;

import java.io.File;

public class SSLClientConfig {
    private SSLClientConfig() {}

    public static void configure() {
        String currentDir = System.getProperty("user.dir");
        System.out.println("Debugging: Working directory: " + currentDir);

        String path = currentDir + File.separator + "src" + File.separator +
                        "main" + File.separator + "resources" + File.separator +
                        "security" + File.separator + "client.truststore";

        File trustStore = new File(path);

        if (trustStore.exists()) {
            System.out.println("Security TrustStore found at: " + path);
            System.setProperty("javax,net.ssl.trustStore", path);
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        }
        else {
            System.err.println("Critical error: TrustStore is not found at: " + path);
            System.err.println("SSL connection will fail. Confirm the path");
        }

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
