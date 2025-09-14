package org.pahappa.systems.kpiTracker.models.goals;

import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "organization_goals")
public class OrganizationGoal extends BaseEntity {
    private String name;
    private String description;
    private ReviewCycle reviewCycle;

    @Column(name = "name",nullable = false,unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description",nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(
            name = "review_cycle_Id"
    )
    public ReviewCycle getReviewCycle() {
        return reviewCycle;
    }

    public void setReviewCycle(ReviewCycle reviewCycle) {
        this.reviewCycle = reviewCycle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationGoal that = (OrganizationGoal) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getReviewCycle(), that.getReviewCycle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getReviewCycle());
    }
}
