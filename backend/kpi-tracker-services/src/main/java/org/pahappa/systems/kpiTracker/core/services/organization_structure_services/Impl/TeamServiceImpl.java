package org.pahappa.systems.kpiTracker.core.services.organization_structure_services.Impl;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamServiceImpl extends GenericServiceImpl<Team> implements TeamService {
    @Override
    public Team saveInstance(Team entityInstance) {
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(Team instance) {
        return true; // To determine if the team can be deleted
    }
}
