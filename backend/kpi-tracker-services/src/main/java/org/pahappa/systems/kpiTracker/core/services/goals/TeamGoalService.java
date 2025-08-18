package org.pahappa.systems.kpiTracker.core.services.goals;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;

public interface TeamGoalService  extends GenericService<TeamGoal> {
    Object getObjectById(String var1);
}
