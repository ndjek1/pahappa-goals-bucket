package org.pahappa.systems.kpiTracker.models.organization_structure;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    private String departmentName;
    private String departmentDescription;
    private User departmentHead;



    @Transient
    private int teamsCount;

    @Transient
    private int memberCount;

    // CONSTRUCTORS
    public Department() {
    }

    public Department(String departmentName, String departmentDescription, User departmentHead) {
        this.departmentName = departmentName;
        this.departmentDescription = departmentDescription;
        this.departmentHead = departmentHead;
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



    public int getTeamsCount() {
        return teamsCount;
    }
    public void setTeamsCount(int teamsCount) {
        this.teamsCount = teamsCount;
    }

    public int getMemberCount() {
        return memberCount;
    }
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        Department that = (Department) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
