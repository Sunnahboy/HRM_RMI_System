package com.hrmrmi.server.replication;

import com.hrmrmi.common.util.Config;
import com.hrmrmi.server.HRMServiceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Backup RMI server.
 * Starts a secondary instance of HRMService for failover purposes.
 */

public class BackupServer {
    // Use a different port for backup server
    private static final int BACKUP_RMI_PORT = 54322;
    private static final String BACKUP_SERVICE_NAME = "HRMServiceBackup";

    public static void main(String[] args) {
        try {
            // Start a separate RMI registry for the backup server
            LocateRegistry.createRegistry(BACKUP_RMI_PORT);

            HRMServiceImpl service = new HRMServiceImpl();

            String rmiUrl = "rmi://localhost:" +
                    BACKUP_RMI_PORT + "/" +
                    BACKUP_SERVICE_NAME;

            Naming.rebind(rmiUrl, service);

            System.out.println("Backup HRM RMI Server running on port "
                    + BACKUP_RMI_PORT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
