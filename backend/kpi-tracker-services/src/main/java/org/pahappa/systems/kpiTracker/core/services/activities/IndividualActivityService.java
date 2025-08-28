package org.pahappa.systems.kpiTracker.core.services.activities;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.activities.IndividualActivity;

public interface IndividualActivityService extends GenericService<IndividualActivity> {
    Object getObjectById(String id);
}
