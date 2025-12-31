//package com.hrmrmi.server.repository;
//
//import com.hrmrmi.common.model.Employee;
//import com.hrmrmi.common.model.FamilyDetails;
//import com.hrmrmi.common.model.Leave;
//import com.hrmrmi.common.util.DBConnection;
//import com.hrmrmi.common.model.Report;
//
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Date;
//@SuppressWarnings({"SqlDialectInspection","CallToPrintStackTrace"})
//public class ReportRepository {
//    public Map<String, Integer> getMonthlyLeaveReport(int year) {
//        Map<String, Integer> report = new HashMap<>();
//
//        String sql = """
//                SELECT TO_CHAR(startDate, 'Month') AS month, COUNT(*) AS count
//                FROM leaves
//                WHERE EXTRACT(YEAR FROM startDate) = ?
//                GROUP BY month
//                ORDER BY MIN(startDate)""";
//
//        try(Connection conn = DBConnection.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, year);
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                report.put(rs.getString("month").trim(), rs.getInt("count"));
//            }
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }
//        return report;
//    }
//
//    public int getEmployeesOnLeaveToday() {
//        String sql = """
//                SELECT COUNT(*) AS count
//                FROM leaves
//                WHERE CURRENT_DATE BETWEEN startDate AND endDate""";
//
//        try(Connection conn = DBConnection.getConnection();
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(sql)) {
//
//            if(rs.next()) {
//                return rs.getInt("count");
//            }
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    public Map<String, Integer> getLeaveStatsByDepartment() {
//        Map<String, Integer> report = new HashMap<>();
//
//        String sql = """
//                SELECT e.department, COUNT(*) AS count
//                FROM leaves 1
//                JOIN employees e ON 1.employeeId = e.id
//                GROUP BY e.department""";
//
//        try (Connection conn = DBConnection.getConnection();
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(sql)) {
//
//            while (rs.next()) {
//                report.put(rs.getString("department"), rs.getInt("count"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return report;
//    }
//
//    public Report generateEmployeeReport(int empId, int year) {
//        Employee emp = null;
//        List<FamilyDetails> familyDetailsList = new ArrayList<>();
//        List<Leave> leaveList = new ArrayList<>();
//        String empName = "Unknown";
//        int leavesTaken = 0;
//        int leaveBalance = 0;
//
//        String sqlEmployee = "SELECT * FROM employees WHERE id = ?";
//        String sqlFam = "SELECT * FROM family_details WHERE employeeId = ?";
//
//        // Uses EXTRACT for PostgreSQL compliance
//        String sqlLeave = "SELECT * FROM leaves WHERE employeeId = ? AND EXTRACT(YEAR FROM startDate) = ?";
//
//        try (Connection conn = DBConnection.getConnection()) {
//
//            // 1. Fetch Employee Profile
//            try (PreparedStatement ps = conn.prepareStatement(sqlEmployee)) {
//                ps.setInt(1, empId);
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    emp = new Employee(
//                            rs.getInt("id"),
//                            rs.getString("firstName"),
//                            rs.getString("lastName"),
//                            rs.getString("email"),
//                            rs.getString("department"),
//                            rs.getString("ic_passport_num"),
//                            rs.getString("position"),
//                            rs.getInt("leaveBalance"), // We will overwrite this shortly
//                            rs.getDouble("salary"),
//                            null, // password hidden
//                            rs.getString("role")
//                    );
//                } else {
//                    return null; // Employee not found
//                }
//            }
//
//            // 2. Fetch Family Details
//            try (PreparedStatement ps = conn.prepareStatement(sqlFam)) {
//                ps.setInt(1, empId);
//                ResultSet rs = ps.executeQuery();
//                while (rs.next()) {
//                    familyDetailsList.add(new FamilyDetails(
//                            rs.getInt("familyId"),
//                            rs.getInt("employeeId"),
//                            rs.getString("name"),
//                            rs.getString("relationship"),
//                            rs.getString("contact")
//                    ));
//                }
//            }
//
//            // 3. Fetch Leave History & Calculate Total
//            try (PreparedStatement ps = conn.prepareStatement(sqlLeave)) {
//                ps.setInt(1, empId);
//                ps.setInt(2, year);
//                ResultSet rs = ps.executeQuery();
//                while (rs.next()) {
//                    Leave l = new Leave(
//                            rs.getInt("leaveId"),
//                            rs.getInt("employeeId"),
//                            rs.getDate("startDate"),
//                            rs.getDate("endDate"),
//                            rs.getString("status"),
//                            rs.getString("reason"),
//                            rs.getInt("totalDays")
//                    );
//                    leaveList.add(l);
//
//                    // Calculate days if Approved
////                    if ("Approved".equalsIgnoreCase(l.getStatus())) {
////                        long diff = l.getEndDate().getTime() - l.getStartDate().getTime();
////                        int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
////                        leavesTaken += days;
////                    }
//                    if ("Approved".equalsIgnoreCase(l.getStatus())) {
//                        leavesTaken += l.getTotalDays();
//                    }
//                }
//            }
//
//            // --- FIX START: Override balance for the specific year ---
//            int yearlyEntitlement = 20; // Default yearly allowance
//            int historicalBalance = yearlyEntitlement - leavesTaken;
//            emp.setLeaveBalance(historicalBalance);
//            // --- FIX END ---
//
//            return new Report(emp, familyDetailsList, leaveList, leavesTaken, new Date(), "HR Admin");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}




package com.hrmrmi.server.repository;

import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.util.DBConnection;
import com.hrmrmi.common.model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

@SuppressWarnings({"SqlDialectInspection","CallToPrintStackTrace"})
public class ReportRepository {

    public Map<String, Integer> getMonthlyLeaveReport(int year) {
        Map<String, Integer> report = new HashMap<>();
        String sql = "SELECT TO_CHAR(startDate, 'Month') AS month, COUNT(*) AS count " +
                "FROM leaves WHERE EXTRACT(YEAR FROM startDate) = ? " +
                "GROUP BY month ORDER BY MIN(startDate)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                report.put(rs.getString("month").trim(), rs.getInt("count"));
            }
        } catch(SQLException e) { e.printStackTrace(); }
        return report;
    }

    public int getEmployeesOnLeaveToday() {
        String sql = "SELECT COUNT(*) AS count FROM leaves WHERE CURRENT_DATE BETWEEN startDate AND endDate";
        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            if(rs.next()) return rs.getInt("count");
        } catch(SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Map<String, Integer> getLeaveStatsByDepartment() {
        Map<String, Integer> report = new HashMap<>();
        String sql = "SELECT e.department, COUNT(*) AS count FROM leaves l " +
                "JOIN employees e ON l.employeeId = e.id GROUP BY e.department";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                report.put(rs.getString("department"), rs.getInt("count"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return report;
    }

    public Report generateEmployeeReport(int empId, int year) {
        System.out.println("DEBUG: Generating Report for EmpID=" + empId + " Year=" + year);

        Employee emp = null;
        List<FamilyDetails> familyDetailsList = new ArrayList<>();
        List<Leave> leaveList = new ArrayList<>();
        int leavesTaken = 0;

        String sqlEmployee = "SELECT * FROM employees WHERE id = ?";
        String sqlFam = "SELECT * FROM family_details WHERE employeeId = ?";
        String sqlLeave = "SELECT * FROM leaves WHERE employeeId = ? AND EXTRACT(YEAR FROM startDate) = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // 1. Fetch Employee
            try (PreparedStatement ps = conn.prepareStatement(sqlEmployee)) {
                ps.setInt(1, empId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    emp = new Employee(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("phoneNumber"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("ic_passport_num"),
                            rs.getString("position"),
                            rs.getInt("leaveBalance"),
                            rs.getDouble("salary"),
                            null,
                            rs.getString("role")
                    );
                } else {
                    System.out.println("DEBUG: Employee not found.");
                    return null;
                }
            }

            // 2. Fetch Family
            try (PreparedStatement ps = conn.prepareStatement(sqlFam)) {
                ps.setInt(1, empId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    familyDetailsList.add(new FamilyDetails(
                            rs.getInt("familyId"),
                            rs.getInt("employeeId"),
                            rs.getString("name"),
                            rs.getString("relationship"),
                            rs.getString("contact")
                    ));
                }
            }

            // 3. Fetch Leaves
            try (PreparedStatement ps = conn.prepareStatement(sqlLeave)) {
                ps.setInt(1, empId);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();

                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    String status = rs.getString("status");
                    int days = rs.getInt("totalDays");

                    System.out.println("DEBUG: Found Leave - Status: '" + status + "', Days: " + days);

                    Leave l = new Leave(
                            rs.getInt("leaveId"),
                            rs.getInt("employeeId"),
                            rs.getDate("startDate"),
                            rs.getDate("endDate"),
                            status,
                            rs.getString("reason"),
                            days
                    );
                    leaveList.add(l);

                    // FIX: Trim the status string to handle database padding
                    if (status != null && "Approved".equalsIgnoreCase(status.trim())) {
                        leavesTaken += days;
                    }
                }
                System.out.println("DEBUG: Total rows found: " + rowCount);
                System.out.println("DEBUG: Calculated Leaves Taken: " + leavesTaken);
            }

            // 4. Override Balance
            int yearlyEntitlement = 20;
            int historicalBalance = yearlyEntitlement - leavesTaken;
            emp.setLeaveBalance(historicalBalance);

            return new Report(emp, familyDetailsList, leaveList, leavesTaken, new Date(), "HR Admin");

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}