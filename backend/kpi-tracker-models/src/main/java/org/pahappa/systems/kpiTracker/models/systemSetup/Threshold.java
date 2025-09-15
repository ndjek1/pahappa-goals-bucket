package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ThresholdLevel;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "thresholds")
public class Threshold extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private ThresholdLevel level;
    private double belowExpectationScore;   // New field
    private double meetExpectationScore;    // New field
    private double exceedsExpectationScore; // New field
    private double needImprovementScore;            // Keep red flag for alerts

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "threshold_level", nullable = false)
    public ThresholdLevel getLevel() {
        return level;
    }

    public void setLevel(ThresholdLevel level) {
        this.level = level;
    }

    @Column(name = "below_expectation_score", nullable = false)
    public double getBelowExpectationScore() {
        return belowExpectationScore;
    }

    public void setBelowExpectationScore(double belowExpectationScore) {
        this.belowExpectationScore = belowExpectationScore;
    }

    @Column(name = "meet_expectation_score", nullable = false)
    public double getMeetExpectationScore() {
        return meetExpectationScore;
    }

    public void setMeetExpectationScore(double meetExpectationScore) {
        this.meetExpectationScore = meetExpectationScore;
    }

    @Column(name = "exceeds_expectation_score", nullable = false)
    public double getExceedsExpectationScore() {
        return exceedsExpectationScore;
    }

    public void setExceedsExpectationScore(double exceedsExpectationScore) {
        this.exceedsExpectationScore = exceedsExpectationScore;
    }

    @Column(name = "need_improvement_score", nullable = false)
    public double getNeedImprovementScore() {
        return needImprovementScore;
    }

    public void setNeedImprovementScore(double needImprovementScore) {
        this.needImprovementScore = needImprovementScore;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Threshold)) return false;
        Threshold that = (Threshold) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLevel(), getBelowExpectationScore(),
                getMeetExpectationScore(), getExceedsExpectationScore(), getNeedImprovementScore());
    }
}
