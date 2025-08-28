package org.pahappa.systems.kpiTracker.models.kpis;

import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "kpis")
public class KPI extends BaseEntity {
    
    private String name;
    private MeasurementUnit measurementUnit;
    private Double targetValue;
    private Double currentValue;
    private Double accomplishmentPercentage;
    private Frequency frequency;
    private Date startDate;
    private Date endDate;
    private Date lastUpdated;

    // References to different goal types
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

    // Review cycle relationship for KPI evaluation periods
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_cycle_id", nullable = true)
    private ReviewCycle reviewCycle;

    // Constructors
    public KPI() {
        super();
    }

    // Getters and Setters
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_unit", nullable = false)
    public MeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @Column(name = "target_value", nullable = false)
    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    @Column(name = "current_value")
    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    @Column(name = "accomplishment_percentage")
    public Double getAccomplishmentPercentage() {
        return accomplishmentPercentage;
    }

    public void setAccomplishmentPercentage(Double accomplishmentPercentage) {
        this.accomplishmentPercentage = accomplishmentPercentage;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
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

    public ReviewCycle getReviewCycle() {
        return reviewCycle;
    }

    public void setReviewCycle(ReviewCycle reviewCycle) {
        this.reviewCycle = reviewCycle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KPI)) return false;
        KPI that = (KPI) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // Methods to calculate accomplishment percentage
    @PrePersist
    @PreUpdate
    public void calculateAccomplishment() {
        if (targetValue != null && targetValue != 0) {
            this.accomplishmentPercentage = (currentValue != null ? currentValue : 0.0) / targetValue * 100;
        }
        this.lastUpdated = new Date();
    }

    @Override
    public String toString() {
        return "KPI{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", measurementUnit=" + measurementUnit +
                ", targetValue=" + targetValue +
                ", currentValue=" + currentValue +
                ", accomplishmentPercentage=" + accomplishmentPercentage +
                ", frequency=" + frequency +
                '}';
    }
}
