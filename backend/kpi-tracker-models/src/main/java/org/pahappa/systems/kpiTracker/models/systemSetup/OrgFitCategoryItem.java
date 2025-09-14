package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "organization_fit_category_items")
public class OrgFitCategoryItem extends BaseEntity {
    private String name;
    private String description;
    private OrgFitCategory orgFitCategory;

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

    @ManyToOne
    @JoinColumn(
            name = "org_fit_category_id",
            nullable = false
    )
    public OrgFitCategory getOrgFitCategory() {
        return orgFitCategory;
    }

    public void setOrgFitCategory(OrgFitCategory orgFitCategory) {
        this.orgFitCategory = orgFitCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrgFitCategoryItem that = (OrgFitCategoryItem) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getOrgFitCategory(), that.getOrgFitCategory());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getOrgFitCategory());
    }
}
