package com.hrmrmi.common.model;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
public class Leave implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;//prevent version mismatch issues when classes are serialized across JVMs.
    private int leaveId;
    private int employeeId;
    private  Date startDate;
    private  Date endDate;
    private  String status;
    private  String reason;

    public Leave(){}

    public Leave(int leaveId, int employeeId, Date startDate, Date endDate,
                 String reason, String status) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    // getters & setters...
    public int getLeaveId() { return leaveId; }
    public void setLeaveId(int leaveId) { this.leaveId = leaveId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    @Override
    public String toString() {
        return "Leave{" + "leaveId=" + leaveId + ", employeeId=" + employeeId +
                ", startDate=" + startDate + ", endDate=" + endDate +
                ", reason='" + reason + '\'' + ", status='" + status + '\'' + '}';
    }

}
