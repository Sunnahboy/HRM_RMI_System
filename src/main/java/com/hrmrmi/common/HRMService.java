package com.hrmrmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.hrmrmi.common.model.*;


public interface HRMService extends Remote{
    //shared func
    String login(String username, String password) throws RemoteException;

    boolean applyLeave(String employeeID, Leave leaveApplication) throws RemoteException;

    boolean updateProfile(Employee employee) throws RemoteException;

    Employee viewProfile(String employeeID) throws RemoteException;

    boolean changePassword(String username, String newPassword) throws RemoteException;

    //HR func
    boolean registerEmployees(String firstName, String lastName, String IC_Passport) throws RemoteException;

    boolean approveLeave(String leaveID, String decision) throws RemoteException;

    Report generateReport(String employeeID, int year) throws RemoteException;

    List<Employee> searchProfile(String keyword) throws RemoteException;

    boolean fireEmployee(String employeeID, String reason);




}
