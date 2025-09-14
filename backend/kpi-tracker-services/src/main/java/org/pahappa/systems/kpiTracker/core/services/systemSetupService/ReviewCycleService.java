package org.pahappa.systems.kpiTracker.core.services.systemSetupService;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;

public interface ReviewCycleService extends GenericService<ReviewCycle> {
    ReviewCycle searchUniqueByPropertyEqual(String property, Object value);
    void updateReviewCycleStatuses();
    Object getObjectById(String var1);
}
