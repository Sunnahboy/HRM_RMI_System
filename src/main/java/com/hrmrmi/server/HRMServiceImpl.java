package com.hrmrmi.server;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.model.Report;

import java.rmi.RemoteException;
import java.util.List;

public class HRMServiceImpl implements HRMService {
    //shared funcs
    @Override
    public String login(String username, String password) throws RemoteException {
        return "";
    }

    @Override
    public boolean applyLeave(String employeeID, Leave leaveApplication) throws RemoteException {
        return false;
    }

    @Override
    public boolean updateProfile(Employee employee) throws RemoteException {
        return false;
    }

    @Override
    public Employee viewProfile(String employeeID) throws RemoteException {
        return null;
    }

    @Override
    public boolean changePassword(String username, String newPassword) throws RemoteException {
        return false;
    }

    //HR funcs
    @Override
    public boolean registerEmployees(String firstName, String lastName, String IC_Passport) throws RemoteException {
        return false;
    }

    @Override
    public boolean approveLeave(String leaveID, String decision) throws RemoteException {
        return false;
    }

    @Override
    public Report generateReport(String employeeID, int year) throws RemoteException {
        return null;
    }

    @Override
    public List<Employee> searchProfile(String keyword) throws RemoteException {
        return List.of();
    }

    @Override
    public boolean fireEmployee(String employeeID, String reason) throws RemoteException {
        return false;
    }
}
