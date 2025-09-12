package org.pahappa.systems.kpiTracker.core.services.systemUsers;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.staff.Staff;

import java.util.List;

public interface StaffService extends GenericService<Staff> {
    public List<Staff> getMembersByTeam(Department department);
    List<Staff> getMembersByTeam(Team team);
    public Staff searchUniqueByPropertyEqual(String property, Object value);
}
