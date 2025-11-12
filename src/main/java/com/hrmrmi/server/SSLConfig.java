package com.hrmrmi.server;
import javax.net.ssl.SSLServerSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.rmi.RemoteException;

public class SSLConfig {
    public static SslRMIServerSocketFactory createServerFactory(){
        //later: supply SSl parameters as needed
        return new SslRMIServerSocketFactory();
    }
    public static SslRMIClientSocketFactory createClientFactory() {
        return new SslRMIClientSocketFactory();
    }
}
