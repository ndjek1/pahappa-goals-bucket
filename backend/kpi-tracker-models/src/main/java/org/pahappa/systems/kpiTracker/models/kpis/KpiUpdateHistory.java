package org.pahappa.systems.kpiTracker.models.kpis;

import org.sers.webutils.model.BaseEntity;
import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Entity to track KPI update history with value changes
 */
@Entity
@Table(name = "kpi_update_history")
public class KpiUpdateHistory extends BaseEntity {

    private KPI kpi;
    private Double value;        // Value at this update
    private Date updateDate;     // When it was updated
    private String comment;      // Optional comment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", nullable = false)
    public KPI getKpi() {
        return kpi;
    }

    public void setKpi(KPI kpi) {
        this.kpi = kpi;
    }

    @Column(name = "value", nullable = false)
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", nullable = false)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "comment", length = 500)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        return Objects.hash(getKpi(), getValue(), getUpdateDate(), getComment());
    }

    @Override
    public String toString() {
        return "KpiUpdateHistory{" +
                "id='" + id + '\'' +
                ", kpi=" + (kpi != null ? kpi.getName() : "null") +
                ", value=" + value +
                ", updateDate=" + updateDate +
                '}';
    }
}
