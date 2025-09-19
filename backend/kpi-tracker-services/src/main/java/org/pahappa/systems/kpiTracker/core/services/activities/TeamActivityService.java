package org.pahappa.systems.kpiTracker.core.services.activities;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;
import org.sers.webutils.model.RecordStatus;

import java.util.List;

public interface TeamActivityService extends GenericService<TeamActivity> {


    List<TeamActivity> searchByPropertyEqual(String property, Object value, RecordStatus recordStatus);
}
