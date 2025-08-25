package org.pahappa.systems.kpiTracker.core.services.systemUsers.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;

import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemUsers.Staff;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StaffServiceImpl extends GenericServiceImpl<Staff> implements StaffService {

    @Override
    public Staff saveInstance(Staff entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(Staff instance) throws OperationFailedException {
        return true;
    }

    @Override
    public List<Staff> getMembersByDepartment(Department department) {
        return super.search(new Search().addFilterEqual("department", department));
    }

}
