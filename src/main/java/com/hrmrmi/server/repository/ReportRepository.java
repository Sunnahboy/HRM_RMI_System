package com.hrmrmi.server.repository;

import com.hrmrmi.common.util.DBConnection;

import java.awt.image.DataBufferDouble;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
