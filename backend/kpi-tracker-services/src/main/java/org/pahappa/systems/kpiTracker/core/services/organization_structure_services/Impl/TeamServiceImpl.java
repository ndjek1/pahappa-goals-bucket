package org.pahappa.systems.kpiTracker.core.services.organization_structure_services.Impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.sers.webutils.model.RecordStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class TeamServiceImpl extends GenericServiceImpl<Team> implements TeamService {
    @Override
    public Team saveInstance(Team entityInstance) {
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(Team instance) {
        return true;
    }

    @Override
    public List<Team> getTeamsByDepartment(Department department) {
        if (department == null) {
            return Collections.emptyList();
        }
        return super.searchByPropertyEqual("department.id", department.getId(), RecordStatus.ACTIVE);
    }
}
