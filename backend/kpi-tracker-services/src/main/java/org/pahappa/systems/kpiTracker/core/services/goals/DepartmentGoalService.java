package org.pahappa.systems.kpiTracker.core.services.goals;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;

public interface DepartmentGoalService extends GenericService<DepartmentGoal> {
    Object getObjectById(String var1);
}
