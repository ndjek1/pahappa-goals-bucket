package org.pahappa.systems.kpiTracker.core.services.systemUsers.impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;

import org.pahappa.systems.kpiTracker.core.services.systemUsers.SystemUserService;
import org.pahappa.systems.kpiTracker.models.systemUsers.SystemUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SystemUserServiceImpl extends GenericServiceImpl<SystemUser> implements SystemUserService {

    @Override
    public SystemUser saveInstance(SystemUser entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(SystemUser instance) throws OperationFailedException {
        return true;
    }
}
