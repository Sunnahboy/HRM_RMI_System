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

        String sql = """
                SELECT TO_CHAR(startDate, 'Month') AS month, COUNT(*) AS count
                FROM leaves
                WHERE EXTRACT(YEAR FROM startDate) = ?
                GROUP BY month
                ORDER BY MIN(startDate)""";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                report.put(rs.getString("month").trim(), rs.getInt("count"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public int getEmployeesOnLeaveToday() {
        String sql = """
                SELECT COUNT(*) AS count
                FROM leaves
                WHERE CURRENT_DATE BETWEEN startDate AND endDate""";

        try(Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            if(rs.next()) {
                return rs.getInt("count");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getLeaveStatsByDepartment() {
        Map<String, Integer> report = new HashMap<>();

        String sql = """
                SELECT e.department, COUNT(*) AS count
                FROM leaves 1
                JOIN employees e ON 1.employeeId = e.id
                GROUP BY e.department""";

        try (Connection conn = DBConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                report.put(rs.getString("department"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
    public Report generateEmployeeReport(int empId, int year) {
        Employee emp = null;
        List<FamilyDetails> familyDetailsList = new ArrayList<>();
        List<Leave> leaveList = new ArrayList<>();
        String empName = "Unknown";
        int leavesTaken = 0;
        int leaveBalance = 0;

        String sqlEmployee = "SELECT * FROM employees WHERE id = ?";
        String sqlFam = "SELECT * FROM family_details WHERE employeeId = ?";

        String sqlLeave = "SELECT * FROM leaves WHERE employeeId = ? AND EXTRACT(YEAR FROM startDate) = ?";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(sqlEmployee)) {
                ps.setInt(1, empId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    emp = new Employee(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("ic_passport_num"),
                            rs.getString("position"),
                            rs.getInt("leaveBalance"),
                            rs.getDouble("salary"),
                            null, // password hidden
                            rs.getString("role")
                    );
                } else {
                    return null; // Employee not found
                }
            }

            // 2. Fetch Family Details
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

            // 3. Fetch Leave History & Calculate Total
            try (PreparedStatement ps = conn.prepareStatement(sqlLeave)) {
                ps.setInt(1, empId);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Leave l = new Leave(
                            rs.getInt("leaveId"),
                            rs.getInt("employeeId"),
                            rs.getDate("startDate"),
                            rs.getDate("endDate"),
                            rs.getString("status"),
                            rs.getString("reason")
                    );
                    leaveList.add(l);

                    // Calculate days if Approved
                    if ("Approved".equalsIgnoreCase(l.getStatus())) {
                        long diff = l.getEndDate().getTime() - l.getStartDate().getTime();
                        int days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
                        leavesTaken += days;
                    }
                }
            }

            return new Report(emp, familyDetailsList, leaveList, leavesTaken, new Date(), "HR Admin");

        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
