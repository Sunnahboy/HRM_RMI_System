package com.hrmrmi.server.repository;

import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.util.DBConnection;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class LeaveRepository {
    public boolean applyLeave(Leave leave) {
        String sql = "INSERT INTO leaves (employeeID, startDate, endDate, status, reason) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leave.getEmployeeId());
            //*****CHECK IF WE USE DATE OR LOCALDATE for the start/end date in Leave!!!*******
            ps.setDate(2, new java.sql.Date(leave.getStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(leave.getEndDate().getTime()));
            ps.setString(4, leave.getStatus());
            ps.setString(5, leave.getReason());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Leave> getLeavesByEmployee(int empId) {
        List<Leave> list = new ArrayList<>();
        String sql = "SELECT * FROM leaves WHERE employeeID = ? ORDER BY startDate DESC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapLeave(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Leave> findPendingLeaves() {
        List<Leave> list = new ArrayList<>();
        String sql = "SELECT * FROM leaves WHERE status = 'PENDING' ORDER BY startDate ASC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Leave leave = new Leave();
                leave.setLeaveId(rs.getInt("leaveId"));
                leave.setEmployeeId(rs.getInt("employeeId"));

                leave.setStartDate(rs.getDate("startDate"));
                leave.setEndDate(rs.getDate("endDate"));
                leave.setReason(rs.getString("reason"));
                leave.setStatus(rs.getString("status"));

                list.add(leave);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean approveLeave(int leaveId) {
        return updateLeaveStatus(leaveId, "Approved");
    }

    public boolean rejectLeave(int leaveId) {
        return updateLeaveStatus(leaveId, "Rejected");
    }

    private boolean updateLeaveStatus(int leaveId, String status) {
        String sql = "UPDATE leaves SET status = ? WHERE leaveId = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, leaveId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteLeave(int leaveId) {
        String sql = "DELETE FROM leaves WHERE leaveId = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leaveId);

            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Leave mapLeave(ResultSet rs) throws SQLException {
        return new Leave(
                rs.getInt("leaveId"),
                rs.getInt("employeeId"),
                rs.getDate("startDate"),
                rs.getDate("endDate"),
                rs.getString("reason"),
                rs.getString("status")
        );
    }

}







