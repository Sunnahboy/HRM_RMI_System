package com.hrmrmi.client.controller;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.Report;
import com.hrmrmi.common.util.Config;


public class HRController {
    private final HRMService service;
    private Employee loggedInHR;

    public HRController() {
        try {
            String rmiUrl = "rmi://" + Config.RMI_HOST + ":"
                    + Config.RMI_PORT + "/"
                    + Config.RMI_NAME;
            service = (HRMService) Naming.lookup(rmiUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to HRM RMI Server", e);
        }
    }

    /**
     * HR login (returns Employee object representing HR user).
     */
    public boolean login(String username, String password) {
        try {
            loggedInHR = service.login(username, password);
            return loggedInHR != null;
        } catch (RemoteException e) {
            System.err.println("RMI error during HR login");
            return false;
        }
    }

    public Employee getLoggedInHR() {
        return loggedInHR;
    }

    /**
     * Register a new employee.
     */
    public boolean registerEmployees(
            String firstName,
            String lastName,
            String icPassport,
            String department,
            String position
    ) {
        try {
            return service.registerEmployees(
                    firstName,
                    lastName,
                    icPassport,
                    department,
                    position
            );
        } catch (RemoteException e) {
            System.err.println("RMI error during employee registration");
            return false;
        }
    }

    /**
     * Approve or reject a leave request.
     */
    public boolean approveLeave(String leaveID, String decision) {
        try {
            return service.approveLeave(leaveID, decision);
        } catch (RemoteException e) {
            System.err.println("RMI error during leave approval");
            return false;
        }
    }

    /**
     * Generate yearly report for an employee.
     */
    public Report generateReport(String employeeID, int year) {
        try {
            return service.generateReport(employeeID, year);
        } catch (RemoteException e) {
            System.err.println("RMI error during report generation");
            return null;
        }
    }

    /**
     * Search employees by keyword (name, ID, department, etc.).
     */
    public List<Employee> searchProfile(String keyword) {
        try {
            return service.searchProfile(keyword);
        } catch (RemoteException e) {
            System.err.println("RMI error during profile search");
            return null;
        }
    }

    /**
     * Fire an employee with a reason.
     */
    public boolean fireEmployee(String employeeID, String reason) {
        try {
            return service.fireEmployee(employeeID, reason);
        } catch (RemoteException e) {
            System.err.println("RMI error during employee termination");
            return false;
        }
    }
}
