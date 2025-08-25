package org.pahappa.systems.kpiTracker.models.activities;

import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityPriority;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityType;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activities")
public class Activity extends BaseEntity {
    
    private String title;
    private String description;
    private ActivityStatus status;
    private ActivityPriority priority;
    private ActivityType activityType;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Date createdDate;
    private Date lastUpdatedDate;
    
    // References to different goal types (similar to KPI.java approach)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_goal_id", nullable = true)
    private OrganizationGoal organizationGoal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_goal_id", nullable = true)
    private DepartmentGoal departmentGoal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_goal_id", nullable = true)
    private TeamGoal teamGoal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "individual_goal_id", nullable = true)
    private IndividualGoal individualGoal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Activity() {
        super();
        this.status = ActivityStatus.PENDING;
        this.priority = ActivityPriority.MEDIUM;
        this.activityType = ActivityType.OPERATIONAL;
        this.createdDate = new Date();
    }

    // Getters and Setters
    @Column(name = "title", nullable = false, length = 200)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public ActivityStatus getStatus() {
        return status;
    }

    public void setStatus(ActivityStatus status) {
        this.status = status;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    public ActivityPriority getPriority() {
        return priority;
    }

    public void setPriority(ActivityPriority priority) {
        this.priority = priority;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type")
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    @Column(name = "planned_start_date")
    @Temporal(TemporalType.DATE)
    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    @Column(name = "planned_end_date")
    @Temporal(TemporalType.DATE)
    public Date getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    @Column(name = "actual_start_date")
    @Temporal(TemporalType.DATE)
    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    @Column(name = "actual_end_date")
    @Temporal(TemporalType.DATE)
    public Date getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "last_updated_date")
    @Temporal(TemporalType.DATE)
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public OrganizationGoal getOrganizationGoal() {
        return organizationGoal;
    }

    public void setOrganizationGoal(OrganizationGoal organizationGoal) {
        this.organizationGoal = organizationGoal;
    }

    public DepartmentGoal getDepartmentGoal() {
        return departmentGoal;
    }

    public void setDepartmentGoal(DepartmentGoal departmentGoal) {
        this.departmentGoal = departmentGoal;
    }

    public TeamGoal getTeamGoal() {
        return teamGoal;
    }

    public void setTeamGoal(TeamGoal teamGoal) {
        this.teamGoal = teamGoal;
    }
    
    public IndividualGoal getIndividualGoal() {
        return individualGoal;
    }

    public void setIndividualGoal(IndividualGoal individualGoal) {
        this.individualGoal = individualGoal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Pre-update hook for validation (following KPI.java pattern)
    @PrePersist
    @PreUpdate
    public void validateDates() {
        if (plannedEndDate != null && plannedStartDate != null && 
            plannedEndDate.before(plannedStartDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (actualEndDate != null && actualStartDate != null && 
            actualEndDate.before(actualStartDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.lastUpdatedDate = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity that = (Activity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", activityType=" + activityType +
                ", user=" + (user != null ? user.getUsername() : "N/A") +
                '}';
    }
}
