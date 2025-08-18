package org.pahappa.systems.kpiTracker.models.organization_structure;

import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;

import javax.persistence.*;

@Entity
@Table(name="teams")
public class Team extends BaseEntity {

    private String teamName;
    private String teamDescription;
    private User teamHead;
    private RecordStatus teamStatus;

    private Department department;

    public Team() {
        super();
    }

    public Team(String teamName, String teamDescription, User teamHead, RecordStatus teamStatus) {
        this.teamName = teamName;
        this.teamDescription = teamDescription;
        this.teamHead = teamHead;
        this.teamStatus = teamStatus;
    }

    // GETTERS AND SETTERS
    @Column(name = "team_name")
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Column(name = "team_description")
    public String getTeamDescription() {
        return teamDescription;
    }
    public void setTeamDescription(String teamDescription) {
        this.teamDescription = teamDescription;
    }

    @ManyToOne
    @JoinColumn(name = "team_head")
    public User getTeamHead() {
        return teamHead;
    }
    public void setTeamHead(User teamHead) {
        this.teamHead = teamHead;
    }

    @Enumerated(EnumType.STRING)
    public RecordStatus getTeamStatus() {
        return teamStatus;
    }
    public void setTeamStatus(RecordStatus teamStatus) {
        this.teamStatus = teamStatus;
    }

    @ManyToOne
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }
}
