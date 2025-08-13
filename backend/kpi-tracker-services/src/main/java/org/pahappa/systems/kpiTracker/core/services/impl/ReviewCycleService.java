package org.pahappa.systems.kpiTracker.core.services.impl;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;

public interface ReviewCycleService extends GenericService<ReviewCycle> {
    Object getObjectById(String var1);
}
