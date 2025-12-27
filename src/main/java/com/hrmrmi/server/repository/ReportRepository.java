package com.hrmrmi.server.repository;

import com.hrmrmi.common.util.DBConnection;
import com.hrmrmi.common.model.Report;

import java.awt.image.DataBufferDouble;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

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
        String sqlEmployee = "SELECT firstName, lastName, leaveBalance FROM employees WHERE id = ?";

        String sqlLeaves = "SELECT SUM(endDate - startDate + 1) AS totalDays " +
                "FROM leaves " +
                "WHERE employeeId = ? AND status = 'Approved' " +
                "AND EXTRACT(YEAR FROM startDate) = ?";

        String empName = "Unknown";
        int leaveBalance = 0;
        int leavesTaken = 0;

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(sqlEmployee)) {
                ps.setInt(1, empId);
                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    empName = rs.getString("firstName") + " " + rs.getString("lastName");
                    leaveBalance = rs.getInt("leaveBalance");
                }
                else {
                    return null;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlLeaves)) {
                ps.setInt(1, empId);
                ps.setInt(2, year);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    leavesTaken = rs.getInt("totalDays");
                }
            }

            return new Report(
                    0, empId, empName, leavesTaken, leaveBalance,
                    new Date(), "HR Admin"
            );
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
