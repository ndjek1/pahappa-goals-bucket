package org.pahappa.systems.kpiTracker.core.services.systemSetupService.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ThresholdService;
import org.pahappa.systems.kpiTracker.models.systemSetup.Threshold;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ThresholdServiceImpl extends GenericServiceImpl<Threshold> implements ThresholdService {

    @Override
    public Threshold saveInstance(Threshold entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(Threshold instance) throws OperationFailedException {
        return true;
    }
}
