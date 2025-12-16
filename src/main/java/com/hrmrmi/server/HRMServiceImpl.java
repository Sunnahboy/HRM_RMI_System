package com.hrmrmi.server;

import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.model.Report;
import com.hrmrmi.server.repository.EmployeeRepository;
import com.hrmrmi.server.repository.LeaveRepository;
import com.hrmrmi.server.repository.ReportRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class HRMServiceImpl extends UnicastRemoteObject implements HRMService {

    private final EmployeeRepository empRepo;
    private final LeaveRepository leaveRepo;
    private final ReportRepository reportRepo;

    protected HRMServiceImpl() throws RemoteException {
        super();

        this.empRepo = new EmployeeRepository();
        this.leaveRepo = new LeaveRepository();
        this.reportRepo = new ReportRepository();
    }

    //shared funcs
    //changed string to boolean and added a return match
    @Override
    public Employee login(String email, String password) throws RemoteException {
        System.out.println("Login attempt for Email: " + email);

        return empRepo.login(email, password);
    }

    @Override
    public boolean applyLeave(String employeeID, Leave leaveApplication) throws RemoteException {
        return leaveRepo.applyLeave(leaveApplication);
    }

    @Override
    public boolean updateProfile(Employee employee) throws RemoteException {
        return empRepo.updateEmployee(employee);
    }

    @Override
    public Employee viewProfile(String employeeID) throws RemoteException {
        return empRepo.getEmployee(employeeID);
    }

    @Override
    public boolean changePassword(String username, String newPassword) throws RemoteException {
        System.out.println("Password change requested for: " + username);
        return true;
    }

    //HR funcs
    @Override
    public boolean registerEmployees(String firstName, String lastName, String IC_Passport, String department, String position) throws RemoteException {
            // employee obj
            Employee emp = new Employee();

            emp.setFirstName(firstName);
            emp.setLastName(lastName);
            emp.setPassportNumber(IC_Passport);
            emp.setDepartment(department);
            emp.setPosition(position);

            if ("HR".equalsIgnoreCase(department)) {
                emp.setRole("HR");
            }
            else {
                emp.setRole("EMPLOYEE");
            }

            // default values for testing
            emp.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@company.com");
            emp.setLeaveBalance(20); // Default 20 days leave
            emp.setSalary(2500);
            emp.setPassword("123456");

            // Send to repository to save via SQL
            return empRepo.registerEmployee(emp);
    }

    @Override
    public boolean approveLeave(String leaveID, String decision) throws RemoteException {
        int id = Integer.parseInt(leaveID);

        if ("Approved".equalsIgnoreCase(decision)) {
            return leaveRepo.approveLeave(id);
        } else {
            return leaveRepo.rejectLeave(id);
        }
    }

    @Override
    public Report generateReport(String employeeID, int year) throws RemoteException {
        return new Report();
    }

    @Override
    public List<Employee> searchProfile(String keyword) throws RemoteException {
        return empRepo.getAllEmployees();
    }

    @Override
    public List<Employee> getAllEmployees() throws RemoteException {
        System.out.println("Collecting all employees record...");
        return empRepo.getAllEmployees();
    }

    @Override
    public boolean fireEmployee(String employeeID, String reason) throws RemoteException {
        int id = Integer.parseInt(employeeID);
        return empRepo.deleteEmployee(id);
    }
}
