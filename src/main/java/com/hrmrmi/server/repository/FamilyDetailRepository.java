package com.hrmrmi.server.repository;

import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FamilyDetailRepository {

    public boolean addFamilyDetail(FamilyDetails fd) {

        String sql = "INSERT INTO family_details (employeeId, name, relationship, contact) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fd.getEmployeeId());
            ps.setString(2, fd.getName());
            ps.setString(3, fd.getRelationship());
            ps.setString(4, fd.getContact());

            // If > 0, save is successful.
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


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