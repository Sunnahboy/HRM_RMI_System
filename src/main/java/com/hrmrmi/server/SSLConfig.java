package com.hrmrmi.server;
import javax.net.ssl.SSLServerSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;

/**
 * SSLConfig provides factory methods for creating SSL-enabled
 * RMI socket factories. These factories ensure that all RMI
 * communication between client and server is encrypted using SSL.
 */

public class SSLConfig {
    public static SslRMIServerSocketFactory createServerFactory(){
        // supply SSl parameters as needed
        return new SslRMIServerSocketFactory();
    }
    public static SslRMIClientSocketFactory createClientFactory() {
        return new SslRMIClientSocketFactory();
    }
}
