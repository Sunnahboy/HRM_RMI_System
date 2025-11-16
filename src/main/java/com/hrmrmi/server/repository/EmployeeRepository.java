package com.hrmrmi.server.repository;

import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    public boolean registerEmployee(Employee emp) {
        String sql = "INSERT INTO employees (id, first name, last name, position, department, salary, Identification) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emp.getId());
            ps.setString(2, emp.getFirstName());
            ps.setString(3, emp.getLastName());
            ps.setString(4, emp.getPosition());
            ps.setString(5, emp.getDepartment());
            ps.setDouble(6, emp.getSalary());
            ps.setString(7, emp.getPassportNumber());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Employee getEmployee(String id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Employee(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("PassportNumber"),
                        rs.getString("position"),
                        rs.getInt("leaveBalance"),
                        rs.getDouble("salary")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()){
                Employee emp = new Employee(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("PassportNumber"),
                        rs.getString("position"),
                        rs.getInt("leaveBalance"),
                        rs.getDouble("salary")
                );
                        list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteEmployee(String id) {
        String sql = "DELETE FROM employees where id = ?";
    }

}
