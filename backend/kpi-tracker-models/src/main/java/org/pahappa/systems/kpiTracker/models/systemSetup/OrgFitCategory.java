package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.OrgFitCategoryType;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.RatingCategory;
import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "organization_fit_categories")
public class OrgFitCategory extends BaseEntity {
    private String name;
    private String description;
    private double weight;
    private RatingCategory ratingCategory;

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(
            name = "org_fit_category_weight",
            nullable = true)
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "rating_category", nullable = false)
    public RatingCategory getRatingCategory() {
        return ratingCategory;
    }

    public void setRatingCategory(RatingCategory ratingCategory) {
        this.ratingCategory = ratingCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrgFitCategory that = (OrgFitCategory) o;
        return Double.compare(getWeight(), that.getWeight()) == 0 && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && getRatingCategory() == that.getRatingCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getWeight(), getRatingCategory());
    }
}
