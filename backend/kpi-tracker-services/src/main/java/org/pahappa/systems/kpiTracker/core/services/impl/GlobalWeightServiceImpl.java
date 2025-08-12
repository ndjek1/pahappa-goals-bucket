package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.GlobalWeightService;

import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GlobalWeightServiceImpl extends GenericServiceImpl<GlobalWeight> implements GlobalWeightService {
    @Override
    public GlobalWeight saveInstance(GlobalWeight entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return merge(entityInstance);
    }

    @Override
    public boolean isDeletable(GlobalWeight instance) throws OperationFailedException {
        return true;
    }

}
