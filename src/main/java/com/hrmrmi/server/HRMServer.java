
/*
 * HRMServer bootstraps the Human Resource Management (HRM) RMI server by
 * configuring SSL security, validating required keystore resources,
 * initializing an SSL-enabled RMI registry, and binding the HRM service
 * implementation for secure remote access by clients.
 */



package com.hrmrmi.server;
import com.hrmrmi.common.util.Config;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.io.File;
import java.rmi.registry.Registry;
@SuppressWarnings({"SqlDialectInspection","CallToPrintStackTrace"})
public class HRMServer {
    public static void main(String[] args) throws RemoteException {
        try {
            // Print configuration status for debugging
            Config.printConfigStatus();
            
            String currentDir = System.getProperty("user.dir");
            String keystorePath = currentDir + File.separator +
                    "src" + File.separator + "main" + File.separator +
                    "resources" + File.separator + "security" + File.separator +
                    "server.keystore";

            File keystore = new File(keystorePath);

            if(!keystore.exists()) {
                System.err.println("CRITICAL: Server keystore not found at path: " + keystorePath);
                System.exit(1);
            }
            System.out.println("Keystore found: " + keystorePath);

            System.setProperty("javax.net.ssl.keyStore", keystorePath);
            System.setProperty("javax.net.ssl.keyStorePassword", "changeit");

            System.setProperty("java.rmi.server.hostname", Config.RMI_HOST);

            HRMServiceImpl service = new HRMServiceImpl();

            // Create SSL-enabled registry
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(
                    Config.RMI_PORT, 
                    com.hrmrmi.server.SSLConfig.createClientFactory(),
                    com.hrmrmi.server.SSLConfig.createServerFactory()
                );
                System.out.println("SSL-enabled RMI Registry started on port " + Config.RMI_PORT);
            } catch (Exception e) {
                System.out.println("Registry already running or SSL-enabled registry failed: " + e.getMessage());
                // Fallback: try to get existing registry with SSL
                registry = LocateRegistry.getRegistry(
                    Config.RMI_HOST, Config.RMI_PORT, 
                    com.hrmrmi.server.SSLConfig.createClientFactory());
            }
            
            // Bind the service (will use SSL automatically due to system properties)
            String url = Config.RMI_NAME;
            registry.rebind(url, service);

            System.out.println("HRM RMI Server bound as " + Config.RMI_NAME + " on port " + Config.RMI_PORT +" at IP: " + Config.RMI_HOST);
            System.out.println("SSL Enabled");

            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }

        }catch(Exception e){
            System.err.println("Server crashed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
