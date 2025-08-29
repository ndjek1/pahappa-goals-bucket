package org.pahappa.systems.kpiTracker.models.kpis;

import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.BaseEntity;
import org.sers.webutils.model.security.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity to track KPI update history with value changes and comments
 */
@Entity
@Table(name = "kpi_update_history")
public class KpiUpdateHistory extends BaseEntity {
    
    private KPI kpi;
    private Double previousValue;
    private Double newValue;
    private String updateComment;
    private Double accomplishmentPercentage;
    private Date updateDate;
    private User updatedByUser;
    private Staff updatedByStaff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false)
    public KPI getKpi() {
        return kpi;
    }

    public void setKpi(KPI kpi) {
        this.kpi = kpi;
    }

    @Column(name = "previous_value")
    public Double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(Double previousValue) {
        this.previousValue = previousValue;
    }

    @Column(name = "new_value", nullable = false)
    public Double getNewValue() {
        return newValue;
    }

    public void setNewValue(Double newValue) {
        this.newValue = newValue;
    }

    @Column(name = "update_comment", length = 1000)
    public String getUpdateComment() {
        return updateComment;
    }

    public void setUpdateComment(String updateComment) {
        this.updateComment = updateComment;
    }

    @Column(name = "accomplishment_percentage")
    public Double getAccomplishmentPercentage() {
        return accomplishmentPercentage;
    }

    public void setAccomplishmentPercentage(Double accomplishmentPercentage) {
        this.accomplishmentPercentage = accomplishmentPercentage;
    }

    @Column(name = "update_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user", nullable = false)
    public User getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(User updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_staff")
    public Staff getUpdatedByStaff() {
        return updatedByStaff;
    }

    public void setUpdatedByStaff(Staff updatedByStaff) {
        this.updatedByStaff = updatedByStaff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiUpdateHistory)) return false;
        KpiUpdateHistory that = (KpiUpdateHistory) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "KpiUpdateHistory{" +
                "id='" + id + '\'' +
                ", kpi=" + (kpi != null ? kpi.getName() : "null") +
                ", previousValue=" + previousValue +
                ", newValue=" + newValue +
                ", updateDate=" + updateDate +
                '}';
    }
}
