package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "review_cycles")
public class ReviewCycle extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private String title;
    private ReviewCycleType type;
    private Date startDate;
    private Date endDate;
    private ReviewCycleStatus status;

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(
            name = "review_cycle_type",
            nullable = true
    )
    public ReviewCycleType getType() {
        return type;
    }

    public void setType(ReviewCycleType type) {
        this.type = type;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "start_date",
            nullable = true
    )
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(
            name = "end_date",
            nullable = true
    )
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(
            name = "review_cycle_status",
            nullable = true
    )
    public ReviewCycleStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewCycleStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewCycle)) return false;
        ReviewCycle that = (ReviewCycle) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
