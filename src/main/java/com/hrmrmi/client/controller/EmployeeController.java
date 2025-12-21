package com.hrmrmi.client.controller;
import com.hrmrmi.common.HRMService;

import java.rmi.Naming;
import java.rmi.RemoteException;
import com.hrmrmi.common.util.Config;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.model.Employee;

public class EmployeeController {
    private final HRMService service;
    private Employee loggedInEmployee;

    public EmployeeController() {
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
            return service.viewProfile(loggedInEmployee.getId());
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
                    loggedInEmployee.getId(),
                    leave
            );
        } catch (RemoteException e) {
            System.err.println("RMI error while applying leave");
            return false;
        }
    }
}
