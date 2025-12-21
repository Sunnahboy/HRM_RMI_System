package com.hrmrmi.common.model;

import java.io.Serial;
import java.io.Serializable;

public class FamilyDetails  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;//prevent version mismatch issues when classes are serialized across JVMs.
    private int familyId;
    private int employeeId;
    private String name;
    private String relationship;
    private String contact;

    public FamilyDetails() {}

    public FamilyDetails(int familyId, int employeeId, String name, String relationship, String contact) {
        this.familyId = familyId;
        this.employeeId = employeeId;
        this.name = name;
        this.relationship = relationship;
        this.contact = contact;
    }

    // getters & setters
    public int getFamilyId() { return familyId; }
    public void setFamilyId(int familyId) { this.familyId = familyId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return "FamilyDetails{" + "familyId=" + familyId + ", employeeId=" + employeeId +
                ", name='" + name + '\'' + ", relationship='" + relationship + '\'' +
                ", contact='" + contact + '\'' + '}';
    }
}

