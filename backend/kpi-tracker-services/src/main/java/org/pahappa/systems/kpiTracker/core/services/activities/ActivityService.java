package org.pahappa.systems.kpiTracker.core.services.activities;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;

public interface ActivityService extends GenericService<Activity> {
    Object getObjectById(String var1);
}
