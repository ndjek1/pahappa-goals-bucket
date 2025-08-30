package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.core.services.impl.OrgFitCategoryMigration;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;

public interface OrgFitCategoryService extends GenericService<OrgFitCategory> {
    Object getObjectById(String var1);
    OrgFitCategory mergeBG(OrgFitCategory entity);
    public OrgFitCategory searchUniqueByPropertyEqual(String property, Object value);
}
