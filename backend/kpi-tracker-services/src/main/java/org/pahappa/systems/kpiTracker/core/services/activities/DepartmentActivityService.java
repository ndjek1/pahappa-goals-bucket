package org.pahappa.systems.kpiTracker.core.services.activities;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;

public interface DepartmentActivityService extends GenericService<DepartmentActivity> {
    Object getObjectById(String id);
}
