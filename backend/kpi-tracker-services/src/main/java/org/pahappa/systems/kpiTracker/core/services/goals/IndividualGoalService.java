package org.pahappa.systems.kpiTracker.core.services.goals;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;

public interface IndividualGoalService extends GenericService<IndividualGoal> {
     Object getObjectById(String id);
}
