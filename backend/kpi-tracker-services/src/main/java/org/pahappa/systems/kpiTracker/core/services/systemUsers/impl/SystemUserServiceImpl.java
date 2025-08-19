package org.pahappa.systems.kpiTracker.core.services.systemUsers.impl;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;

import org.pahappa.systems.kpiTracker.core.services.systemUsers.SystemUserService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemUsers.SystemUser;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public List<SystemUser> getMembersByDepartment(Department department) {
        return super.search(new Search().addFilterEqual("department", department));
    }

}
