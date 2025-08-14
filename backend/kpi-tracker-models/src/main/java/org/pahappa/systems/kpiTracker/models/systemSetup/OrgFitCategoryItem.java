package org.pahappa.systems.kpiTracker.models.systemSetup;

import org.sers.webutils.model.BaseEntity;

import javax.persistence.*;

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
}
