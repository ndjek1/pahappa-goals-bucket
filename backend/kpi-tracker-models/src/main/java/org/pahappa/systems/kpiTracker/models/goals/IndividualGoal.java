package org.pahappa.systems.kpiTracker.models.goals;

import org.sers.webutils.model.security.User;
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
    private User staff;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_goal_id")
    public DepartmentGoal getParent() {
        return parent;
    }

    public void setParent(DepartmentGoal parent) {
        this.parent = parent;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_goal_id")
    public TeamGoal getTeamGoal() {
        return teamGoal;
    }

    public void setTeamGoal(TeamGoal teamGoal) {
        this.teamGoal = teamGoal;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }
}
