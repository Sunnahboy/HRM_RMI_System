package com.hrmrmi.client.controller;
import com.hrmrmi.client.HRMServiceProxy;
import com.hrmrmi.common.HRMService;
import java.rmi.RemoteException;
import java.util.List;

import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.model.Employee;


/**
 * EmployeeController acts as the client-side controller for employee operations.
 * It mediates between the JavaFX user interface and the remote HRM services,
 * managing authentication state and delegating requests through a fault-tolerant
 * RMI proxy that supports automatic server failover.
 */

public class EmployeeController {
    private final HRMService service;
    private Employee loggedInEmployee;

    public EmployeeController() {
        System.out.println(" Initializing EmployeeController with dynamic failover...");
        
        // Use the dynamic proxy for automatic failover
        service = new HRMServiceProxy();
        
        System.out.println(" EmployeeController initialized with dynamic RMI proxy");
    }

    public void setLoggedInEmployee(Employee employee) {
        this.loggedInEmployee = employee;
        System.out.println("   [Controller] User context set to: " + employee.getFirstName());
    }


    /**
     * Login and cache the logged-in employee.
     */
    public boolean login(String username, String password) {
        try {
            loggedInEmployee = service.login(username, password);
            return loggedInEmployee != null;
        } catch (RemoteException e) {
            System.err.println("RMI error during login");
            return false;
        }
    }

    /**
     * Returns the logged-in employee object.
     */
    public Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    /**
     * Fetch profile from server (fresh copy).
     */
    public Employee viewProfile() {
        if (loggedInEmployee == null) return null;

        try {
            return service.viewProfile(String.valueOf(loggedInEmployee.getId()));
        } catch (RemoteException e) {
            System.err.println("RMI error while fetching profile");
            return null;
        }
    }

    /**
     * Submit leave application.
     */
    public boolean applyLeave(Leave leave) {
        if (loggedInEmployee == null) return false;

        try {
            return service.applyLeave(
                    String.valueOf(loggedInEmployee.getId()),
                    leave
            );
        } catch (RemoteException e) {
            System.err.println("RMI error while applying leave");
            return false;
        }
    }


    /**
     * Read-only employee directory (used by Employee GUI).
     */
    public List<Employee> getDirectory() {
        try {
            // Empty keyword = return all employees (server-side logic)
            return service.searchProfile("");
        } catch (RemoteException e) {
            System.err.println("RMI error while fetching employee directory");
            return null;
        }
    }


    public List<Leave> getMyLeaves() {
        if (loggedInEmployee == null) return List.of();

        try {
            return service.getMyLeaves(String.valueOf(loggedInEmployee.getId()));
        } catch (RemoteException e) {
            return List.of();
        }
    }

    public boolean updateProfile(Employee employee) {
        try {
            boolean ok = service.updateProfile(employee);
            if (ok) loggedInEmployee = employee;
            return ok;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Get family details for logged-in employee
     */
    public List<FamilyDetails> getFamilyDetails() {
        if (loggedInEmployee == null) return List.of();

        try {
            return service.getFamilyDetails(String.valueOf(loggedInEmployee.getId()));
        } catch (RemoteException e) {
            System.err.println("RMI error while fetching family details");
            return List.of();
        }
    }

    /**
     * Save family detail for logged-in employee
     */
    public boolean saveFamilyDetail(int employeeId, String name, String relationship, String contact) {
        try {
            FamilyDetails detail = new FamilyDetails();
            detail.setEmployeeId(employeeId);
            detail.setName(name);              // ← Correct field name
            detail.setRelationship(relationship);
            detail.setContact(contact);        // ← Correct field name

            return service.saveFamilyDetail(detail);
        } catch (Exception e) {
            System.err.println("Error while saving family details: " + e.getMessage());
            return false;
        }
    }
}
