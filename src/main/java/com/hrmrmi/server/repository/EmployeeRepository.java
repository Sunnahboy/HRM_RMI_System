/*
 * FamilyDetailRepository serves as the data access layer for employee family
 * details. It encapsulates transactional database operations, supports
 * update-or-insert (upsert) behavior, and maintains data integrity by
 * handling duplicate records and controlled commit/rollback logic.
 */




package com.hrmrmi.server.repository;
import com.hrmrmi.common.model.Employee;
import com.hrmrmi.common.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings({"SqlDialectInspection","CallToPrintStackTrace"})
public class EmployeeRepository {

    /* ===================== LOGIN ===================== */

    public Employee login(String email, String password) {
        String sql = "SELECT * FROM employees WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapEmployee(rs, true); // include password for auth
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ===================== CREATE ===================== */

    public boolean registerEmployee(Employee emp) {
        String sql = """
                INSERT INTO employees
                (firstName, lastName, phoneNumber, email, department, ic_passport_num,
                 position, leaveBalance, salary, password, role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getPhoneNumber());
            ps.setString(4, emp.getEmail());
            ps.setString(5, emp.getDepartment());
            ps.setString(6, emp.getPassportNumber());
            ps.setString(7, emp.getPosition());
            ps.setInt(8, emp.getLeaveBalance());
            ps.setDouble(9, emp.getSalary());
            ps.setString(10, emp.getPassword());
            ps.setString(11, emp.getRole());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== READ ===================== */

    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapEmployee(rs, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Employee> searchEmployee(String keyword) {
        List <Employee> list = new ArrayList<>();

        String sql = "SELECT * FROM employees WHERE firstname ILIKE ? OR lastname ILIKE ? OR department ILIKE ? OR email ILIKE ? OR position ILIKE ? OR role ILIKE ? OR CAST(id AS TEXT) ILIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            for(int i = 1; i<= 7; i++) {
                ps.setString(i, searchPattern);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Employee(
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
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ===================== UPDATE ===================== */

    public boolean updateEmployee(Employee emp) {
        String sql = """
                UPDATE employees SET
                    firstName = ?,
                    lastName = ?,
                    phoneNumber = ?,
                    email = ?,
                    department = ?,
                    ic_passport_num = ?,
                    position = ?,
                    leaveBalance = ?,
                    salary = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getFirstName());
            ps.setString(2, emp.getLastName());
            ps.setString(3, emp.getPhoneNumber());
            ps.setString(4, emp.getEmail());
            ps.setString(5, emp.getDepartment());
            ps.setString(6, emp.getPassportNumber());
            ps.setString(7, emp.getPosition());
            ps.setInt(8, emp.getLeaveBalance());
            ps.setDouble(9, emp.getSalary());
            ps.setInt(10, emp.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== PASSWORD MANAGEMENT ===================== */

    public boolean changePassword(int employeeId, String newPassword) {
        String sql = "UPDATE employees SET password = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, employeeId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasPassword(int employeeId) {
        String sql = "SELECT password FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                return password != null && !password.trim().isEmpty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserPassword(String username) {
        String sql = "SELECT password FROM employees WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean changePassword(String username, String newPassword) {
        String sql = "UPDATE employees SET password = ? WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== DELETE ===================== */

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== MAPPER ===================== */

    /**
     * Maps ResultSet to Employee.
     * @param includePassword true ONLY for login/authentication
     */
    private Employee mapEmployee(ResultSet rs, boolean includePassword) throws SQLException {
        return new Employee(
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
                includePassword ? rs.getString("password") : null,
                rs.getString("role")
        );
    }
}
