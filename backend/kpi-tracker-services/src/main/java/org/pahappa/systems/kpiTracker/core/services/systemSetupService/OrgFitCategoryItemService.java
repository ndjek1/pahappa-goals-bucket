package org.pahappa.systems.kpiTracker.core.services.systemSetupService;


import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;

public interface OrgFitCategoryItemService extends GenericService<OrgFitCategoryItem> {

    public OrgFitCategoryItem merge(OrgFitCategoryItem entity);
}
