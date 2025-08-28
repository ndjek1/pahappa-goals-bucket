package org.pahappa.systems.kpiTracker.core.services.activities;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;

public interface TeamActivityService extends GenericService<TeamActivity> {
    Object getObjectById(String id);
}
