package org.pahappa.systems.kpiTracker.models.goals;

import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "team_goals")
public class TeamGoal extends BaseEntity {
    private String name;
    private String description;
    private double contributionWeight;
    private GoalStatus status;
    private DepartmentGoal parent;
    private Team team;

    @Column(name = "name",nullable = false,unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description",nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "contribution_weight", nullable = false)
    public double getContributionWeight() {
        return contributionWeight;
    }

    public void setContributionWeight(double contributionWeight) {
        this.contributionWeight = contributionWeight;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id", nullable = false)
    public DepartmentGoal getParent() {
        return parent;
    }

    public void setParent(DepartmentGoal parent) {
        this.parent = parent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamGoal)) return false;
        TeamGoal that = (TeamGoal) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
