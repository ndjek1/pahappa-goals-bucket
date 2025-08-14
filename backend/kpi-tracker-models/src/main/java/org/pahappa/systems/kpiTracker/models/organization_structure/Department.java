package org.pahappa.systems.kpiTracker.models.organization_structure;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    private String departmentName;
    private String departmentDescription;
    private User departmentHead;
    private RecordStatus departmentStatus;



    // CONSTRUCTORS
    public Department() {
        super();
    }

    public Department(String departmentName, String departmentDescription, RecordStatus departmentStatus) {
        this.departmentName = departmentName;
        this.departmentDescription = departmentDescription;
        this.departmentStatus = departmentStatus;
    }


    // GETTERS AND SETTERS
    @Column(name = "department_name")
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Column(name="department_description")
    public String getDepartmentDescription() {
        return departmentDescription;
    }
    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    @ManyToOne
    @JoinColumn(name = "department_head")
    public User getDepartmentHead() {
        return departmentHead;
    }
    public void setDepartmentHead(User departmentHead) {
        this.departmentHead = departmentHead;
    }


    @Enumerated(EnumType.STRING)
    public RecordStatus getDepartmentStatus() {
        return departmentStatus;
    }
    public void setDepartmentStatus(RecordStatus departmentStatus) {
        this.departmentStatus = departmentStatus;
    }

}
