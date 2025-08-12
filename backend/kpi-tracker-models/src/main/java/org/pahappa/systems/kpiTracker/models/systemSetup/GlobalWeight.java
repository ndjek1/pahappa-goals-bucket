package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "global_weights")
public class GlobalWeight extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private double mboWeight;
    private  double OrgFitWeight;
    private ReviewCycle reviewCycle;

    @Column(name = "mbo_weight")
    public double getMboWeight() {
        return mboWeight;
    }

    public void setMboWeight(double mboWeight) {
        this.mboWeight = mboWeight;
    }

    @Column(name = "org_fit_weight")
    public double getOrgFitWeight() {
        return OrgFitWeight;
    }

    public void setOrgFitWeight(double orgFitWeight) {
        OrgFitWeight = orgFitWeight;
    }

    @ManyToOne
    @JoinColumn(
            name = "review_cycle_Id",
            nullable = true
    )
    public ReviewCycle getReviewCycle() {
        return reviewCycle;
    }

    public void setReviewCycle(ReviewCycle reviewCycle) {
        this.reviewCycle = reviewCycle;
    }
}
