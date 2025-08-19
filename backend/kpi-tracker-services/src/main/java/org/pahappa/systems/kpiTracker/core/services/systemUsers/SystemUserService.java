package org.pahappa.systems.kpiTracker.core.services.systemUsers;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemUsers.SystemUser;

import java.util.List;

public interface SystemUserService extends GenericService<SystemUser> {
    public List<SystemUser> getMembersByDepartment(Department department);
}
