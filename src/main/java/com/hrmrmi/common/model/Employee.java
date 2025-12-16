package com.hrmrmi.common.model;
import java.io.Serial;
import java.io.Serializable;

public class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;//prevent version mismatch issues when classes are serialized across JVMs.

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String PassportNumber;
    private String position;
    private int leaveBalance;
    private double salary;
    private String password;
    private String role;

    public Employee() {}//to safely rebuild objects when sending it across the network or reading from the database without needing to pass constructor argument manually

    public Employee(int id, String firstName, String lastName, String email,String department,String PassportNumber, String position, int leaveBalance, double salary, String password, String role){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.PassportNumber = PassportNumber;
        this.password = password;
        this.role = role;
        this.department = department;
        this.position = position;
        this.leaveBalance = leaveBalance;
        this.salary = salary;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPassportNumber() { return PassportNumber; }
    public void setPassportNumber(String PassportNumber) { this.PassportNumber = PassportNumber; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public int getLeaveBalance() { return leaveBalance; }
    public void setLeaveBalance(int leaveBalance) { this.leaveBalance = leaveBalance; }
    public double getSalary() { return salary; }
    public void setSalary(int salary) {this.salary = salary; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + firstName + " " + lastName + '\'' +
                ", role='" + role + '\'' +
                ", dept='" + department + '\'' +
                ", leaveBal=" + leaveBalance +
                '}';
    }
}

