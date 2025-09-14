package org.pahappa.systems.kpiTracker.core.services.systemSetupService.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.OrgFitCategoryService;

import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrgFitCategoryServiceImpl extends GenericServiceImpl<OrgFitCategory> implements OrgFitCategoryService {

    @Override
    public OrgFitCategory saveInstance(OrgFitCategory entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(OrgFitCategory instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Object getObjectById(String id) {
        return super.getInstanceByID(id);
    }


}
