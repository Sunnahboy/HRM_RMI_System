
package com.hrmrmi.server.repository;

import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"SqlDialectInspection","CallToPrintStackTrace"})
public class FamilyDetailRepository {

    /**
     * UPDATE if exists, INSERT if new (UPSERT pattern with transaction handling)
     */
    public boolean addFamilyDetail(FamilyDetails fd) {
        System.out.println("\n🗄️  [REPO] addFamilyDetail()");
        System.out.println("   Employee ID: " + fd.getEmployeeId());
        System.out.println("   Name: " + fd.getName());

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First, check if employee already has a family detail
            String checkSql = "SELECT familyId FROM family_details WHERE employeeId = ?";

            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, fd.getEmployeeId());
                ResultSet rs = checkPs.executeQuery();

                boolean success;
                if (rs.next()) {
                    // Record exists - UPDATE
                    int familyId = rs.getInt("familyId");
                    success = updateFamilyDetailInTransaction(fd, familyId, conn);
                } else {
                    // Record doesn't exist - INSERT
                    success = insertFamilyDetailInTransaction(fd, conn);
                }

                if (success) {
                    conn.commit();
                    System.out.println("   Transaction committed successfully\n");
                } else {
                    conn.rollback();
                    System.out.println("  Transaction rolled back\n");
                }

                return success;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback error: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Connection close error: " + e.getMessage());
            }
        }
    }

    /**
     * INSERT new family detail in transaction
     */
    private boolean insertFamilyDetailInTransaction(FamilyDetails fd, Connection conn) {
        String sql = "INSERT INTO family_details (employeeId, name, relationship, contact) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("Inserting new record...");

            ps.setInt(1, fd.getEmployeeId());
            ps.setString(2, fd.getName());
            ps.setString(3, fd.getRelationship());
            ps.setString(4, fd.getContact());

            int rowsAffected = ps.executeUpdate();
            System.out.println("   ✓ Rows affected: " + rowsAffected);
            System.out.println("   ✓ Insert successful: " + (rowsAffected > 0));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
            return false;
        }
    }

    /**
     * UPDATE existing family detail in transaction
     */
    private boolean updateFamilyDetailInTransaction(FamilyDetails fd, int familyId, Connection conn) {
        String sql = "UPDATE family_details SET name = ?, relationship = ?, contact = ? " +
                "WHERE familyId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("Updating existing record (ID: " + familyId + ")...");

            ps.setString(1, fd.getName());
            ps.setString(2, fd.getRelationship());
            ps.setString(3, fd.getContact());
            ps.setInt(4, familyId);

            int rowsAffected = ps.executeUpdate();
            System.out.println("   ✓ Rows affected: " + rowsAffected);
            System.out.println("   ✓ Update successful: " + (rowsAffected > 0));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(" Update error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    private boolean insertFamilyDetail(FamilyDetails fd) {
        String sql = "INSERT INTO family_details (employeeId, name, relationship, contact) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println(" Inserting new record...");

            ps.setInt(1, fd.getEmployeeId());
            ps.setString(2, fd.getName());
            ps.setString(3, fd.getRelationship());
            ps.setString(4, fd.getContact());

            int rowsAffected = ps.executeUpdate();
            System.out.println("   ✓ Rows affected: " + rowsAffected);
            System.out.println("   ✓ Insert successful: " + (rowsAffected > 0) + "\n");

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(" Insert error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    private boolean updateFamilyDetail(FamilyDetails fd, int familyId) {
        String sql = "UPDATE family_details SET name = ?, relationship = ?, contact = ? " +
                "WHERE familyId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Updating existing record (ID: " + familyId + ")...");

            ps.setString(1, fd.getName());
            ps.setString(2, fd.getRelationship());
            ps.setString(3, fd.getContact());
            ps.setInt(4, familyId);

            int rowsAffected = ps.executeUpdate();
            System.out.println("   ✓ Rows affected: " + rowsAffected);
            System.out.println("   ✓ Update successful: " + (rowsAffected > 0) + "\n");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clean up duplicate family detail records
     * Keeps only the latest record (highest familyId) for each employee
     */
    public boolean cleanupDuplicates() {
        String sql = "DELETE FROM family_details WHERE familyId NOT IN (" +
                "SELECT MAX(familyId) FROM family_details GROUP BY employeeId)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Cleaning up duplicate family detail records...");
            int deleted = ps.executeUpdate();
            System.out.println("✓ Deleted " + deleted + " duplicate records\n");
            return true;

        } catch (SQLException e) {
            System.out.println("Cleanup error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetch family details for an employee
     */
    public List<FamilyDetails> getFamilyDetails(int empId) {
        List<FamilyDetails> list = new ArrayList<>();

        String sql = "SELECT * FROM family_details WHERE employeeId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new FamilyDetails(
                        rs.getInt("familyId"),
                        rs.getInt("employeeId"),
                        rs.getString("name"),
                        rs.getString("relationship"),
                        rs.getString("contact")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}