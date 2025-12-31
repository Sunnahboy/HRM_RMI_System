package com.hrmrmi.client;
import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.*;
import com.hrmrmi.common.util.Config;
import com.hrmrmi.server.SSLConfig;
import java.rmi.RemoteException;
import java.util.List;
import javax.rmi.ssl.SslRMIClientSocketFactory;

/**
 * HRMServiceProxy implements the HRMService interface and acts as a
 * fault-tolerant client-side access layer. It dynamically switches
 * between primary and backup RMI servers upon failure while preserving
 * secure SSL-based communication.
 */


public class HRMServiceProxy implements HRMService {
    
    private HRMService primaryService;
    private HRMService backupService;
    private HRMService currentService;
    private String currentServer; // "PRIMARY" or "BACKUP"
    private boolean primaryAvailable = false;
    private boolean backupAvailable = false;
    
    public HRMServiceProxy() {
        // Configure SSL for client
        SSLClientConfig.configure();
        initializeConnections();
    }
    
    /**
     * Initialize connections to both servers and determine initial active server
     */
    private void initializeConnections() {
        // Create SSL client socket factory
        SslRMIClientSocketFactory sslClientFactory = SSLConfig.createClientFactory();
        
        // Try to connect to both servers
        try {
            // Use LocateRegistry.getRegistry() with SSL client factory
            java.rmi.registry.Registry primaryRegistry = java.rmi.registry.LocateRegistry.getRegistry(
                Config.RMI_HOST, Config.RMI_PORT, sslClientFactory);
            primaryService = (HRMService) primaryRegistry.lookup(Config.RMI_NAME);
            primaryAvailable = true;
            System.out.println("Connected to PRIMARY server via SSL");
        } catch (Exception e) {
            primaryAvailable = false;
            System.err.println("PRIMARY server unavailable: " + e.getMessage());
        }
        
        try {
            // Use LocateRegistry.getRegistry() with SSL client factory
            java.rmi.registry.Registry backupRegistry = java.rmi.registry.LocateRegistry.getRegistry(
                "localhost", Config.BACKUP_RMI_PORT, sslClientFactory);
            backupService = (HRMService) backupRegistry.lookup(Config.BACKUP_RMI_NAME);
            backupAvailable = true;
            System.out.println("Connected to BACKUP server via SSL");
        } catch (Exception e) {
            backupAvailable = false;
            System.err.println(" BACKUP server unavailable: " + e.getMessage());
            
            // More detailed error analysis for SSL issues
            if (e.getMessage() != null && e.getMessage().contains("SSL")) {
                System.err.println("   🔧 SSL HANDLER TROUBLESHOOTING:");
                System.err.println("   - Check if backup server is running with SSL enabled");
                System.err.println("   - Verify SSL certificates match on client and server");
                System.err.println("   - Ensure client truststore contains server certificate");
            } else if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
                System.err.println("   🔧 CONNECTION TROUBLESHOOTING:");
                System.err.println("   - Ensure backup server is running on port 54322");
                System.err.println("   - Check firewall settings");
            }
        }
        
        // Determine initial active server
        if (primaryAvailable) {
            currentService = primaryService;
            currentServer = "PRIMARY";
            System.out.println(" Using PRIMARY server as active");
        } else if (backupAvailable) {
            currentService = backupService;
            currentServer = "BACKUP";
            System.out.println("Using BACKUP server as active");
        } else {
            throw new RuntimeException("No servers available - both PRIMARY and BACKUP failed to connect");
        }
    }
    
    /**
     * Execute method with automatic failover on RemoteException
     */
    private <T> T executeWithFailover(FailoverOperation<T> operation) throws RemoteException {
        int maxRetries = 2; // Try current server, then switch to other server
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            // Check server health before each attempt
            checkServerHealth();
            
            System.out.println("🔄 Attempt " + (attempt + 1) + ": Using " + currentServer + " server");
            
            try {
                T result = operation.execute(currentService);
                System.out.println(" Operation successful on " + currentServer + " server");
                return result;
                
            } catch (RemoteException e) {
                System.err.println(" " + currentServer + " server failed on attempt " + (attempt + 1) + ": " + e.getMessage());
                
                if (attempt == maxRetries - 1) {
                    // Last attempt failed
                    System.err.println(" All retry attempts failed!");
                    break;
                }
                
                // Try to switch to the other server
                System.out.println("🔄 Attempting to switch to other server...");
                if (switchToOtherServer()) {
                    System.out.println(" Successfully switched to " + currentServer + " server, retrying operation...");
                    // Retry with the new server
                } else {
                    System.err.println(" Failed to switch to other server - no backup available");
                    break;
                }
            }
        }
        
        // All servers failed
        System.err.println(" FAILOVER FAILED: All servers unavailable");
        System.err.println("   Primary available: " + primaryAvailable);
        System.err.println("   Backup available: " + backupAvailable);
        System.err.println("   Current server: " + currentServer);
        
        throw new RemoteException("All servers failed - no available server for operation");
    }
    
    /**
     * Switch to the other available server
     */
    private boolean switchToOtherServer() {
        System.out.println(" Checking switch possibilities...");
        System.out.println("   Current server: " + currentServer);
        System.out.println("   Primary available: " + primaryAvailable);
        System.out.println("   Backup available: " + backupAvailable);
        
        if (currentServer.equals("PRIMARY")) {
            System.out.println(" Attempting to switch from PRIMARY to BACKUP...");
            if (backupAvailable && backupService != null) {
                currentService = backupService;
                currentServer = "BACKUP";
                System.out.println("Successfully switched to BACKUP server");
                return true;
            } else {
                System.err.println(" Cannot switch to BACKUP - service is " + (backupService == null ? "null" : "not null") + ", available: " + backupAvailable);
            }
        } else if (currentServer.equals("BACKUP")) {
            System.out.println("🔄 Attempting to switch from BACKUP to PRIMARY...");
            if (primaryAvailable && primaryService != null) {
                currentService = primaryService;
                currentServer = "PRIMARY";
                System.out.println(" Successfully switched to PRIMARY server");
                return true;
            } else {
                System.err.println(" Cannot switch to PRIMARY - service is " + (primaryService == null ? "null" : "not null") + ", available: " + primaryAvailable);
            }
        } else {
            System.err.println(" Unknown current server: " + currentServer);
        }
        
        System.err.println("Server switch failed!");
        return false;
    }
    
    /**
     * Check server availability and update status flags
     */
    private void checkServerHealth() {
        System.out.println(" Checking server health...");
        
        // Check primary server
        if (primaryService != null) {
            try {
                // Try a simple operation to test if server is alive
                primaryService.getAllEmployees(); // This should work if server is alive
                if (!primaryAvailable) {
                    primaryAvailable = true;
                    System.out.println(" PRIMARY server is now available");
                }
            } catch (Exception e) {
                if (primaryAvailable) {
                    primaryAvailable = false;
                    System.out.println(" PRIMARY server is now unavailable: " + e.getMessage());
                }
            }
        } else if (primaryAvailable) {
            primaryAvailable = false;
            System.out.println(" PRIMARY service reference is null");
        }
        
        // Check backup server
        if (backupService != null) {
            try {
                // Try a simple operation to test if server is alive
                backupService.getAllEmployees(); // This should work if server is alive
                if (!backupAvailable) {
                    backupAvailable = true;
                    System.out.println("✅ BACKUP server is now available");
                }
            } catch (Exception e) {
                if (backupAvailable) {
                    backupAvailable = false;
                    System.out.println("❌ BACKUP server is now unavailable: " + e.getMessage());
                    
                    // Provide SSL-specific troubleshooting
                    if (e.getMessage() != null && e.getMessage().contains("SSL")) {
                        System.out.println("   🔧 SSL TROUBLESHOOTING:");
                        System.out.println("   - Backup server may not be using SSL properly");
                        System.out.println("   - Check if backup server keystore is configured");
                        System.out.println("   - Verify hostname matches SSL certificate");
                    }
                }
            }
        } else if (backupAvailable) {
            backupAvailable = false;
            System.out.println("❌ BACKUP service reference is null");
        }
        
        System.out.println("Health check results:");
        System.out.println("   Primary: " + (primaryAvailable ? " Available" : "Unavailable"));
        System.out.println("   Backup: " + (backupAvailable ? " Available" : " Unavailable"));
        System.out.println("   Current: " + currentServer);
    }
    

    // ========== HRMService Interface Methods ==========
    
    @Override
    public Employee login(String username, String password) throws RemoteException {
        return executeWithFailover(service -> service.login(username, password));
    }
    
    @Override
    public boolean applyLeave(String employeeID, Leave leaveApplication) throws RemoteException {
        return executeWithFailover(service -> service.applyLeave(employeeID, leaveApplication));
    }
    
    @Override
    public boolean updateProfile(Employee employee) throws RemoteException {
        return executeWithFailover(service -> service.updateProfile(employee));
    }
    
    @Override
    public Employee viewProfile(String employeeID) throws RemoteException {
        return executeWithFailover(service -> service.viewProfile(employeeID));
    }
    
    @Override
    public boolean changePassword(String username, String newPassword) throws RemoteException {
        return executeWithFailover(service -> service.changePassword(username, newPassword));
    }
    
    @Override
    public String getUserPassword(String username) throws RemoteException {
        return executeWithFailover(service -> service.getUserPassword(username));
    }
    
    @Override
    public boolean registerEmployees(String firstName, String lastName, String phoneNumber, String IC_Passport,
                                   String department, String position) throws RemoteException {
        return executeWithFailover(service -> service.registerEmployees(firstName, lastName, phoneNumber, IC_Passport,
                                                                        department, position));
    }
    
    @Override
    public boolean approveLeave(String leaveID, String decision) throws RemoteException {
        return executeWithFailover(service -> service.approveLeave(leaveID, decision));
    }
    
    @Override
    public List<Leave> getAllPendingLeaves() throws RemoteException {
        return executeWithFailover(HRMService::getAllPendingLeaves);
    }
    
    @Override
    public List<Leave> getMyLeaves(String employeeID) throws RemoteException {
        return executeWithFailover(service -> service.getMyLeaves(employeeID));
    }
    
    @Override
    public Report generateReport(String employeeID, int year) throws RemoteException {
        return executeWithFailover(service -> service.generateReport(employeeID, year));
    }
    
    @Override
    public List<Employee> searchProfile(String keyword) throws RemoteException {
        return executeWithFailover(service -> service.searchProfile(keyword));
    }
    
    @Override
    public List<Employee> getAllEmployees() throws RemoteException {
        return executeWithFailover(HRMService::getAllEmployees);
    }
    
    @Override
    public boolean fireEmployee(String employeeID, String reason) throws RemoteException {
        return executeWithFailover(service -> service.fireEmployee(employeeID, reason));
    }
    
    @Override
    public boolean updateEmployeeStatus(String employeeID, String newDept, String newPosition, 
                                      double newSalary) throws RemoteException {
        return executeWithFailover(service -> service.updateEmployeeStatus(employeeID, newDept, 
                                                                          newPosition, newSalary));
    }
    
    @Override
    public List<FamilyDetails> getFamilyDetails(String employeeID) throws RemoteException {
        return executeWithFailover(service -> service.getFamilyDetails(employeeID));
    }
    
    @Override
    public boolean saveFamilyDetail(FamilyDetails details) throws RemoteException {
        return executeWithFailover(service -> service.saveFamilyDetail(details));
    }
    
    @Override
    public boolean cleanupFamilyDetailDuplicates() throws RemoteException {
        return executeWithFailover(HRMService::cleanupFamilyDetailDuplicates);
    }
    
    // Functional interface for operations
    @FunctionalInterface
    private interface FailoverOperation<T> {
        T execute(HRMService service) throws RemoteException;
    }

}