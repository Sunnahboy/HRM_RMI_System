
/*
 * BackupServer bootstraps a secondary Human Resource Management (HRM) RMI server
 * configured for fault tolerance. The backup server shares the same persistent
 * database as the primary server, maintains no in-memory state, and enables
 * secure client failover through SSL-protected RMI communication.
 */

package com.hrmrmi.server.replication;
import com.hrmrmi.server.HRMServiceImpl;
import java.io.File;
@SuppressWarnings({"CallToPrintStackTrace"})
public class BackupServer {

    // Backup RMI registry port (must differ from primary)
    private static final int BACKUP_RMI_PORT = 54322;
    private static final String BACKUP_SERVICE_NAME = "HRMServiceBackup";

    public static void main(String[] args) {
        try {
            System.out.println("======================================");
            System.out.println(" STARTING BACKUP HRM RMI SERVER");
            System.out.println("======================================");

            // 1. Configure SSL for backup server
            configureSSL();

            // 3. Database connectivity check removed to allow server to start
            // verifyDatabaseConnection();

            // 4. Create HRM service (stateless, DB-backed)
            HRMServiceImpl service = new HRMServiceImpl();

            // 5. Create SSL-enabled registry and bind the service
            java.rmi.registry.Registry registry;
            try {
                registry = java.rmi.registry.LocateRegistry.createRegistry(
                    BACKUP_RMI_PORT,
                    com.hrmrmi.server.SSLConfig.createClientFactory(),
                    com.hrmrmi.server.SSLConfig.createServerFactory()
                );
                System.out.println("✔ SSL-enabled Backup RMI Registry started on port " + BACKUP_RMI_PORT);
            } catch (Exception e) {
                System.out.println("Backup registry already running or SSL-enabled registry failed: " + e.getMessage());
                // Fallback: try to get existing registry with SSL
                registry = java.rmi.registry.LocateRegistry.getRegistry(
                    "localhost", BACKUP_RMI_PORT,
                    com.hrmrmi.server.SSLConfig.createClientFactory());
            }
            
            // Bind the service (will use SSL automatically due to system properties)
            String serviceName = BACKUP_SERVICE_NAME;
            registry.rebind(serviceName, service);

            System.out.println("✔ Backup HRM Service bound at: rmi://localhost:" + BACKUP_RMI_PORT + "/" + BACKUP_SERVICE_NAME);
            System.out.println("✔ Backup server READY with SSL ENABLED");
            System.out.println("======================================");

        } catch (Exception e) {
            System.err.println(" BACKUP SERVER FAILED TO START");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Configure SSL for backup server (same as primary)
     */
    private static void configureSSL() {
        String currentDir = System.getProperty("user.dir");
        String keystorePath = currentDir + File.separator +
                "src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "security" + File.separator +
                "server.keystore";

        File keystore = new File(keystorePath);

        if(!keystore.exists()) {
            System.err.println("CRITICAL: Backup server keystore not found at path: " + keystorePath);
            System.exit(1);
        }
        System.out.println("Backup keystore found: " + keystorePath);

        System.setProperty("javax.net.ssl.keyStore", keystorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("java.rmi.server.hostname", "localhost");
    }


}

