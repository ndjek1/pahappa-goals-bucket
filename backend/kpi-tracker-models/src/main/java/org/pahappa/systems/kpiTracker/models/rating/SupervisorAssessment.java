package org.pahappa.systems.kpiTracker.models.rating;

import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "supervisor_assessments")
public class SupervisorAssessment extends BaseEntity {
    private ReviewCycle reviewCycle;
    private Staff supervisor;
    private Staff staff;
    private double score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_cycle_id", nullable = false)
    public ReviewCycle getReviewCycle() {
        return reviewCycle;
    }

    public void setReviewCycle(ReviewCycle reviewCycle) {
        this.reviewCycle = reviewCycle;
    }

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    public Staff getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Staff supervisor) {
        this.supervisor = supervisor;
    }

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Column(name = "score",nullable = false)
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
