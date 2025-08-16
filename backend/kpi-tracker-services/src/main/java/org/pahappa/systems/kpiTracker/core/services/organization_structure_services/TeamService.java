package org.pahappa.systems.kpiTracker.core.services.organization_structure_services;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;

import java.util.List;

public interface TeamService extends GenericService<Team> {
    List<Team> getTeamsByDepartment(Department department);
}
