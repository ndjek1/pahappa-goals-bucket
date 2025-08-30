package org.pahappa.systems.kpiTracker.views.goals;

import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;

public class ItemRating {
    private OrgFitCategoryItem item;
    private Integer rating; // bound directly to <p:rating>

    public ItemRating(OrgFitCategoryItem item, Integer rating) {
        this.item = item;
        this.rating = rating;
    }
    public OrgFitCategoryItem getItem() { return item; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}

