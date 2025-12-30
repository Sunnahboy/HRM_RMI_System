
/*
 * HRMServiceImpl provides the concrete server-side implementation of the HRMService
 * remote interface. It acts as the business logic layer in the RMI architecture,
 * coordinating requests from remote clients and interacting with repository
 * components to manage employees, leave records, reports, and related HR data.
 */

package com.hrmrmi.server;
import com.hrmrmi.common.HRMService;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.model.Report;
import com.hrmrmi.server.repository.EmployeeRepository;
import com.hrmrmi.server.repository.FamilyDetailRepository;
import com.hrmrmi.server.repository.LeaveRepository;
import com.hrmrmi.server.repository.ReportRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class HRMServiceImpl extends UnicastRemoteObject implements HRMService {

    private final EmployeeRepository empRepo;
    private final LeaveRepository leaveRepo;
    private final ReportRepository reportRepo;
    private final FamilyDetailRepository familyRepo;




    public HRMServiceImpl() throws RemoteException {
        super(); //  keep SSL disabled until fully tested

        this.empRepo = new EmployeeRepository();
        this.leaveRepo = new LeaveRepository();
        this.reportRepo = new ReportRepository();
        this.familyRepo = new FamilyDetailRepository();

    }

    /* ===================== SHARED ===================== */

    @Override
    public List<FamilyDetails> getFamilyDetails(String employeeID) throws RemoteException {
        int id = Integer.parseInt(employeeID);
        return familyRepo.getFamilyDetails(id);
    }

    @Override
    public boolean saveFamilyDetail(FamilyDetails details) throws RemoteException {
        return familyRepo.addFamilyDetail(details);
    }

    /**
     * Clean up duplicate family detail records
     */
    public boolean cleanupFamilyDetailDuplicates() throws RemoteException {
        System.out.println("🧹 [SERVICE] Cleaning up family detail duplicates...");
        return familyRepo.cleanupDuplicates();
    }


    @Override
    public Employee login(String email, String password) throws RemoteException {
        System.out.println("Login attempt for: " + email);
        return empRepo.login(email, password);
    }

    @Override
    public boolean applyLeave(String employeeID, Leave leave) throws RemoteException {
        try {
            int empId = Integer.parseInt(employeeID);
            leave.setEmployeeId(empId); // 🔴 CRITICAL FIX
            return leaveRepo.applyLeave(leave);
        } catch (NumberFormatException e) {
            System.out.println("Invalid employee ID");
            return false;
        }
    }

    @Override
    public boolean updateProfile(Employee employee) throws RemoteException {
        return empRepo.updateEmployee(employee);
    }

    @Override
    public Employee viewProfile(String employeeID) throws RemoteException {
        try {
            int id = Integer.parseInt(employeeID);
            return empRepo.getEmployeeById(id); // 🔴 FIXED
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean changePassword(String username, String newPassword) throws RemoteException {
        System.out.println("Password change request for user: " + username);
        return empRepo.changePassword(username, newPassword);
    }

    @Override
    public String getUserPassword(String username) throws RemoteException {
        System.out.println("Getting password hash for user: " + username);
        return empRepo.getUserPassword(username);
    }

    /* ===================== HR ONLY ===================== */

    @Override
    public boolean registerEmployees(
            String firstName,
            String lastName,
            String icPassport,
            String department,
            String position
    ) throws RemoteException {

        Employee emp = new Employee();
        emp.setFirstName(firstName);
        emp.setLastName(lastName);
        emp.setPassportNumber(icPassport);
        emp.setDepartment(department);
        emp.setPosition(position);
        if (department != null && "HR".equalsIgnoreCase(department.trim())) {
            emp.setRole("HR");
        } else {
            emp.setRole("employee");
        }
        emp.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@company.com");
        emp.setLeaveBalance(20);
        emp.setSalary(2500);
        emp.setPassword("123456");

        return empRepo.registerEmployee(emp);
    }

    @Override
    public boolean approveLeave(String leaveID, String decision) throws RemoteException {
        int id = Integer.parseInt(leaveID);
        return "Approved".equalsIgnoreCase(decision)
                ? leaveRepo.approveLeave(id)
                : leaveRepo.rejectLeave(id);
    }

        @Override
    public List<Leave> getAllPendingLeaves() throws RemoteException {
        System.out.println("Fetching all employees pending leaves...");
        return leaveRepo.findPendingLeaves();
    }


    @Override
    public List<Leave> getMyLeaves(String employeeID) throws RemoteException {
        try {
            int id = Integer.parseInt(employeeID);
            return leaveRepo.getLeavesByEmployee(id);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }


    @Override
    public Report generateReport(String employeeID, int year) throws RemoteException {
        try {
            int id = Integer.parseInt(employeeID);
            return reportRepo.generateEmployeeReport(id, year);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public List<Employee> searchProfile(String keyword) throws RemoteException {
        return empRepo.searchEmployee(keyword);
    }

    @Override
    public List<Employee> getAllEmployees() throws RemoteException {
        return List.of();
    }

    @Override
    public boolean fireEmployee(String employeeID, String reason) throws RemoteException {
        try {
            return empRepo.deleteEmployee(Integer.parseInt(employeeID));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean updateEmployeeStatus(String employeeID, String newDept, String newPosition, double newSalary) throws RemoteException {
        return false;
    }
}
