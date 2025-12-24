package com.hrmrmi.server.repository;

import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.model.FamilyDetails;
import com.hrmrmi.common.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    public Employee login(String email, String password) {
        String sql = "SELECT * FROM employees WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new Employee(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("ic_passport_num"),
                        rs.getString("position"),
                        rs.getInt("leaveBalance"),
                        rs.getDouble("salary"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerEmployee(Employee emp) {
        String sql = "INSERT INTO employees (firstName, lastName, email, department, ic_passport_num, position, leaveBalance, salary, password, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getDepartment());
            ps.setString(5, emp.getPassportNumber());
            ps.setString(6, emp.getPosition());
            ps.setInt(7, emp.getLeaveBalance());
            ps.setDouble(8, emp.getSalary());
            ps.setString(9, emp.getPassword());
            ps.setString(10, emp.getRole());

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
                        rs.getString("ic_passport_num"),
                        rs.getString("position"),
                        rs.getInt("leaveBalance"),
                        rs.getDouble("salary"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> searchEmployee(String keyword) {
        List <Employee> list = new ArrayList<>();

        String sql = "SELECT * FROM employees WHERE firstname ILIKE ? OR lastname ILIKE ? OR department ILIKE ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("ic_passport_num"),
                        rs.getString("position"),
                        rs.getInt("leaveBalance"),
                        rs.getDouble("salary"),
                        null,
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
                        rs.getString("ic_passport_num"),
                        rs.getString("position"),
                        rs.getInt("leaveBalance"),
                        rs.getDouble("salary"),
                        null,
                        rs.getString("role")
                );
                        list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET firstName = ?, lastName = ?, email = ?, department = ?, passport_number = ?, position = ?, leaveBalance = ?, salary = ? WHERE id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getDepartment());
            ps.setString(5, emp.getPassportNumber());
            ps.setString(6, emp.getPosition());
            ps.setInt(7, emp.getLeaveBalance());
            ps.setDouble(8, emp.getSalary());
            ps.setInt(9, emp.getId());

            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // HR update emp
//    public boolean updateStatus(int id, String dept, String pos, double salary) {
//        String sql = "UPDATE employees SET department = ?, position = ?, salary = ? WHERE id = ?";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, dept);
//            ps.setString(2, pos);
//            ps.setDouble(3, salary);
//            ps.setInt(4, id); // The Target
//
//            return ps.executeUpdate() > 0;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getString("ic_passport_num"),
                rs.getString("position"),
                rs.getInt("leave_balance"),
                rs.getDouble("salary"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}