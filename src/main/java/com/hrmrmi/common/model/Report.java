package com.hrmrmi.common.model;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;//prevent version mismatch issues when classes are serialized across JVMs.
    private int reportId;
    private int employeeId;
    private String employeeName;
    private int totalLeavesTaken;
    private int remainingLeaveBalance;
    private Date generatedDate;
    private String generatedBy;  // e.g., "HR" or "System"

    public Report() {}

    public Report(int reportId, int employeeId, String employeeName,
                  int totalLeavesTaken, int remainingLeaveBalance,
                  Date generatedDate, String generatedBy) {
        this.reportId = reportId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalLeavesTaken = totalLeavesTaken;
        this.remainingLeaveBalance = remainingLeaveBalance;
        this.generatedDate = generatedDate;
        this.generatedBy = generatedBy;
    }

    // Getters and setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public int getTotalLeavesTaken() { return totalLeavesTaken; }
    public void setTotalLeavesTaken(int totalLeavesTaken) { this.totalLeavesTaken = totalLeavesTaken; }

    public int getRemainingLeaveBalance() { return remainingLeaveBalance; }
    public void setRemainingLeaveBalance(int remainingLeaveBalance) { this.remainingLeaveBalance = remainingLeaveBalance; }

    public Date getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(Date generatedDate) { this.generatedDate = generatedDate; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", totalLeavesTaken=" + totalLeavesTaken +
                ", remainingLeaveBalance=" + remainingLeaveBalance +
                ", generatedDate=" + generatedDate +
                ", generatedBy='" + generatedBy + '\'' +
                '}';
    }
}

