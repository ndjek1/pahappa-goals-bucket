package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ThresholdLevel;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "thresholds")
public class Threshold extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private ThresholdLevel level;
    private double minScore;
    private double redFlagScore;

    @Enumerated(EnumType.ORDINAL)
    @Column(
            name = "threshold_level",
            nullable = false
    )
    public ThresholdLevel getLevel() {
        return level;
    }

    public void setLevel(ThresholdLevel level) {
        this.level = level;
    }

    @Column(
            name = "min_score"
    )
    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    @Column(
            name = "red_flag_score"
    )
    public double getRedFlagScore() {
        return redFlagScore;
    }

    public void setRedFlagScore(double redFlagScore) {
        this.redFlagScore = redFlagScore;
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
        return super.hashCode();
    }
}
