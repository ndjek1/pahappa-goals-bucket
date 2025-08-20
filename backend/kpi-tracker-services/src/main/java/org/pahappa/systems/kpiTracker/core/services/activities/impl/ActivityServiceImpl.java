package org.pahappa.systems.kpiTracker.core.services.activities.impl;

import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityServiceImpl extends GenericServiceImpl<Activity> implements ActivityService {
    @Override
    public Activity saveInstance(Activity entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(Activity instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Object getObjectById(String id) {
        return super.getInstanceByID(id);
    }
}
