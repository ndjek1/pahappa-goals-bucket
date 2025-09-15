package org.pahappa.systems.kpiTracker.models.activities;

import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityPriority;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityType;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "individual_activities")
public class IndividualActivity extends BaseEntity {
    private String title;
    private String description;
    private ActivityStatus status;
    private ActivityPriority priority;
    private ActivityType activityType;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private double targetValue;
    private double actualValue;
    private IndividualGoal individualGoal;
    private Staff staff;

    @Column(name = "title", nullable = false, length = 200)
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "description", columnDefinition = "TEXT")
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
    @Column(name = "priority", nullable = false)
    public ActivityPriority getPriority() {
        return priority;
    }
    public void setPriority(ActivityPriority priority) {
        this.priority = priority;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    public ActivityType getActivityType() {
        return activityType;
    }
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "planned_start_date", nullable = false)
    public Date getPlannedStartDate() {
        return plannedStartDate;
    }
    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "planned_end_date", nullable = false)
    public Date getPlannedEndDate() {
        return plannedEndDate;
    }
    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    @Column(name = "target_value", nullable = false)
    public double getTargetValue() {
        return targetValue;
    }
    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    @Column(name = "actual_value")
    public double getActualValue() {
        return actualValue;
    }
    public void setActualValue(double actualValue) {
        this.actualValue = actualValue;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "individual_goal_id", nullable = true)
    public IndividualGoal getIndividualGoal() {
        return individualGoal;
    }

    public void setIndividualGoal(IndividualGoal individualGoal) {
        this.individualGoal = individualGoal;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_id", nullable = true)
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IndividualActivity that = (IndividualActivity) o;
        return Double.compare(getTargetValue(), that.getTargetValue()) == 0 && Double.compare(getActualValue(), that.getActualValue()) == 0 && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && getStatus() == that.getStatus() && getPriority() == that.getPriority() && getActivityType() == that.getActivityType() && Objects.equals(getPlannedStartDate(), that.getPlannedStartDate()) && Objects.equals(getPlannedEndDate(), that.getPlannedEndDate()) && Objects.equals(getIndividualGoal(), that.getIndividualGoal()) && Objects.equals(getStaff(), that.getStaff());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getPriority(), getActivityType(), getPlannedStartDate(), getPlannedEndDate(), getTargetValue(), getActualValue(), getIndividualGoal(), getStaff());
    }
}
