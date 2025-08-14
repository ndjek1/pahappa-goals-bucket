package org.pahappa.systems.kpiTracker.core.services.organization_structure_services.Impl;

import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartmentServiceImpl extends GenericServiceImpl<Department> implements DepartmentService {
    @Override
    public Department saveInstance(Department entityInstance) {
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(Department instance) {
        return true; // To determine if the department can be deleted
    }
}
