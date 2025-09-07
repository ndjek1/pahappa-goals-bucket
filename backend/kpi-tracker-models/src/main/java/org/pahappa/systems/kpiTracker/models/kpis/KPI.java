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
import java.util.List;

@Entity
@Table(name = "kpis")
public class KPI extends BaseEntity {
    
    private String name;
    private MeasurementUnit measurementUnit;
    private Double targetValue;
    private Double currentValue;
    private Frequency frequency;
    private Date startDate;
    private Date endDate;
    private Date lastUpdated;
    private Double weight;

    // References to different goal types

    private OrganizationGoal organizationGoal;
    

    private DepartmentGoal departmentGoal;
    

    private TeamGoal teamGoal;


    private IndividualGoal individualGoal;

    // Review cycle relationship for KPI evaluation periods

    private ReviewCycle reviewCycle;

    // Constructors
    public KPI() {
        super();
        this.weight = 1.0;
    }

    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<KpiUpdateHistory> updateHistory;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_goal_id", nullable = true)
    public OrganizationGoal getOrganizationGoal() {
        return organizationGoal;
    }

    public void setOrganizationGoal(OrganizationGoal organizationGoal) {
        this.organizationGoal = organizationGoal;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_goal_id", nullable = true)
    public DepartmentGoal getDepartmentGoal() {
        return departmentGoal;
    }

    public void setDepartmentGoal(DepartmentGoal departmentGoal) {
        this.departmentGoal = departmentGoal;
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
    @JoinColumn(name = "individual_goal_id", nullable = true)
    public IndividualGoal getIndividualGoal() {
        return individualGoal;
    }

    public void setIndividualGoal(IndividualGoal individualGoal) {
        this.individualGoal = individualGoal;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_cycle_id", nullable = true)
    public ReviewCycle getReviewCycle() {
        return reviewCycle;
    }

    public void setReviewCycle(ReviewCycle reviewCycle) {
        this.reviewCycle = reviewCycle;
    }

    @Column(name = "weight", nullable = false)
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Transient
    public double getProgress() {
        if (targetValue == null || targetValue <= 0) {
            return 0;
        }
        if (currentValue == null) {
            return 0;
        }
        return Math.round(((currentValue / targetValue) * 100) * 100.0) / 100.0;
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



    @Override
    public String toString() {
        return "KPI{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", measurementUnit=" + measurementUnit +
                ", targetValue=" + targetValue +
                ", currentValue=" + currentValue +
                ", frequency=" + frequency +
                '}';
    }
}
