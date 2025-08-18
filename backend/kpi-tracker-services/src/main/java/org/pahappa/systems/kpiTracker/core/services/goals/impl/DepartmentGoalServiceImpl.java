package org.pahappa.systems.kpiTracker.core.services.goals.impl;

import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class DepartmentGoalServiceImpl extends GenericServiceImpl<DepartmentGoal> implements DepartmentGoalService {
    @Override
    public DepartmentGoal saveInstance(DepartmentGoal entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(DepartmentGoal instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Object getObjectById(String id) {
        return super.getInstanceByID(id);
    }
}
