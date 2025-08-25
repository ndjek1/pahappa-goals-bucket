package org.pahappa.systems.kpiTracker.core.services.systemUsers;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemUsers.Staff;

import java.util.List;

public interface StaffService extends GenericService<Staff> {
    public List<Staff> getMembersByDepartment(Department department);
    public Staff searchUniqueByPropertyEqual(String property, Object value);
}
