package org.pahappa.systems.kpiTracker.models.goals;

import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "individual_goals")
public class IndividualGoal extends BaseEntity {
    private String name;
    private String description;
    private double contributionWeight;
    private GoalStatus status;
    private DepartmentGoal parent;
    private TeamGoal teamGoal;
    private Staff staff;

    public IndividualGoal() {
        this.status = GoalStatus.PENDING;
    }

    @Column(name = "name",nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "contribution_weight")
    public double getContributionWeight() {
        return contributionWeight;
    }

    public void setContributionWeight(double contributionWeight) {
        this.contributionWeight = contributionWeight;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_goal_id")
    public DepartmentGoal getParent() {
        return parent;
    }

    public void setParent(DepartmentGoal parent) {
        this.parent = parent;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_goal_id", nullable = true)
    public TeamGoal getTeamGoal() {
        return teamGoal;
    }

    public void setTeamGoal(TeamGoal teamGoal) {
        this.teamGoal = teamGoal;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id")
    public Staff getStaff() {
        return this.staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
