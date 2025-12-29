
/*
 * LeaveRepository acts as the data access layer for leave management.
 * It encapsulates SQL operations and business validations required to
 * apply, extend, approve, reject, and retrieve employee leave records
 * while ensuring data consistency and rule enforcement.
 */


package com.hrmrmi.server.repository;
import com.hrmrmi.common.model.Leave;
import com.hrmrmi.common.util.DBConnection;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"SqlDialectInspection","CallToPrintStackTrace"})
public class LeaveRepository {

    /**
     * Apply for leave with validations
     * - Start date must be in the future (not today)
     * - Employee cannot have multiple PENDING leaves
     * - New leave cannot overlap with existing leaves
     */
    public boolean applyLeave(Leave leave) {
        System.out.println("\n🗄️  [REPO] applyLeave()");
        System.out.println("   Employee ID: " + leave.getEmployeeId());
        System.out.println("   Start Date: " + leave.getStartDate());
        System.out.println("   End Date: " + leave.getEndDate());

        // Validation 1: Start date must be in the future (not today)
        LocalDate startDate = convertToLocalDate(leave.getStartDate());
        LocalDate today = LocalDate.now();

        if (!startDate.isAfter(today)) {
            System.out.println("    REJECT: Start date must be in the future (not today or past)");
            return false;
        }

        // Validation 2: Check for existing PENDING leave
        if (hasPendingLeave(leave.getEmployeeId())) {
            System.out.println("    REJECT: Employee already has a pending leave");
            return false;
        }

        // Validation 3: Check for overlapping leaves
        if (overlapsExistingLeave(leave.getEmployeeId(), leave.getStartDate(), leave.getEndDate())) {
            System.out.println("    REJECT: Dates overlap with existing leave");
            return false;
        }

        // All validations passed - insert
        String sql = "INSERT INTO leaves (employeeId, startDate, endDate, status, reason) VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leave.getEmployeeId());
            ps.setDate(2, new java.sql.Date(leave.getStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(leave.getEndDate().getTime()));
            ps.setString(4, leave.getStatus());
            ps.setString(5, leave.getReason());

            int rowsAffected = ps.executeUpdate();
            System.out.println("   ✓ Leave applied successfully\n");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("    SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if employee already has a PENDING leave
     */
    private boolean hasPendingLeave(int employeeId) {
        String sql = "SELECT COUNT(*) as count FROM leaves WHERE employeeId = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking pending leaves: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if new leave overlaps with existing leaves (excluding REJECTED)
     * Two date ranges overlap if: newStart <= existingEnd AND newEnd >= existingStart
     */
    private boolean overlapsExistingLeave(int employeeId, java.util.Date newStart, java.util.Date newEnd) {
        System.out.println("   🔍 Checking overlap with:");
        System.out.println("   New range: " + newStart + " to " + newEnd);
        
        // First, check if any overlaps exist
        String countSql = "SELECT COUNT(*) as count FROM leaves WHERE employeeId = ? " +
                "AND status != 'REJECTED' " +
                "AND startDate <= ? AND endDate >= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {

            ps.setInt(1, employeeId);
            ps.setDate(2, new java.sql.Date(newEnd.getTime()));
            ps.setDate(3, new java.sql.Date(newStart.getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("   Found " + count + " overlapping leaves");
                
                if (count > 0) {
                    // If overlaps exist, get details
                    String detailSql = "SELECT startDate, endDate, status FROM leaves WHERE employeeId = ? " +
                            "AND status != 'REJECTED' " +
                            "AND startDate <= ? AND endDate >= ?";
                    
                    try (PreparedStatement detailPs = conn.prepareStatement(detailSql)) {
                        detailPs.setInt(1, employeeId);
                        detailPs.setDate(2, new java.sql.Date(newEnd.getTime()));
                        detailPs.setDate(3, new java.sql.Date(newStart.getTime()));
                        
                        ResultSet detailRs = detailPs.executeQuery();
                        while (detailRs.next()) {
                            System.out.println("     Overlap with: " + detailRs.getDate("startDate") +
                                             " to " + detailRs.getDate("endDate") + 
                                             " (" + detailRs.getString("status") + ")");
                        }
                    }
                }
                return count > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking overlapping leaves: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ============ EXTENSION METHODS ============

    /**
     * Extend an existing leave with validation
     */
    public boolean extendLeave(int leaveId, int employeeId, java.sql.Date newEndDate) {
        System.out.println("\n [REPO] extendLeave()");
        System.out.println("   Leave ID: " + leaveId);
        System.out.println("   Employee ID: " + employeeId);
        System.out.println("   New End Date: " + newEndDate);

        // Validation Step 1: Leave exists and belongs to employee
        Leave leave = getLeaveById(leaveId);
        if (leave == null) {
            System.out.println("    Leave not found");
            return false;
        }

        if (leave.getEmployeeId() != employeeId) {
            System.out.println("    Leave does not belong to this employee");
            return false;
        }

        // Validation Step 2: Leave status must be PENDING or APPROVED
        if (!leave.getStatus().equals("PENDING") && !leave.getStatus().equals("APPROVED")) {
            System.out.println("    Leave status is " + leave.getStatus() + " (cannot extend)");
            return false;
        }

        // Validation Step 3: New end date must be after current end date
        if (!newEndDate.after(leave.getEndDate())) {
            System.out.println("    New end date must be after current end date (" + leave.getEndDate() + ")");
            return false;
        }

        // Validation Step 4: New end date cannot include weekends
        if (containsWeekend(leave.getStartDate(), newEndDate)) {
            System.out.println("    Extended period includes weekends");
            return false;
        }

        // Validation Step 5: Extension does not overlap another leave
        if (overlapsOtherLeave(employeeId, leaveId, leave.getStartDate(), newEndDate)) {
            System.out.println("    Extension overlaps with another leave");
            return false;
        }

        // All validations passed - perform update
        return updateLeaveEndDate(leaveId, newEndDate);
    }

    /**
     * Get leave by ID
     */
    private Leave getLeaveById(int leaveId) {
        String sql = "SELECT * FROM leaves WHERE leaveId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, leaveId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapLeave(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave: " + e.getMessage());
        }
        return null;
    }

    /**
     * Update leave end date
     */
    private boolean updateLeaveEndDate(int leaveId, java.sql.Date newEndDate) {
        String sql = "UPDATE leaves SET endDate = ? WHERE leaveId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, newEndDate);
            ps.setInt(2, leaveId);

            int rowsAffected = ps.executeUpdate();
            System.out.println("   ✓ Updated rows: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating leave: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if date range contains weekends (Saturday=6, Sunday=7)
     */
    private boolean containsWeekend(java.util.Date startDate, java.util.Date endDate) {
        LocalDate start = convertToLocalDate(startDate);
        LocalDate end = convertToLocalDate(endDate);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                System.out.println("     Weekend found: " + date + " (" + day + ")");
                return true;
            }
        }
        return false;
    }

    /**
     * Check if date range overlaps with other leaves (excluding current leave)
     * Two date ranges overlap if: newStart <= existingEnd AND newEnd >= existingStart
     */
    private boolean overlapsOtherLeave(int employeeId, int excludeLeaveId, java.util.Date startDate, java.util.Date endDate) {
        String sql = "SELECT COUNT(*) as count FROM leaves WHERE " +
                "employeeId = ? AND leaveId != ? AND " +
                "startDate <= ? AND endDate >= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setInt(2, excludeLeaveId);
            ps.setDate(3, new java.sql.Date(endDate.getTime()));
            ps.setDate(4, new java.sql.Date(startDate.getTime()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    System.out.println("     Found " + count + " overlapping leave(s)");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking overlaps: " + e.getMessage());
        }
        return false;
    }

    /**
     * Convert java.util.Date to LocalDate safely
     * Handles both java.util.Date and java.sql.Date
     */
    private LocalDate convertToLocalDate(java.util.Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // ============ EXISTING METHODS (UNCHANGED) ============

    public List<Leave> getLeavesByEmployee(int empId) {
        List<Leave> list = new ArrayList<>();
        String sql = "SELECT * FROM leaves WHERE employeeId = ? ORDER BY startDate DESC";

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
