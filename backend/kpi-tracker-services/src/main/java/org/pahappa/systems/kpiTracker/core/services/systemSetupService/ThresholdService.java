package org.pahappa.systems.kpiTracker.core.services.systemSetupService;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.systemSetup.Threshold;

public interface ThresholdService extends GenericService<Threshold> {
    public Threshold searchUniqueByPropertyEqual(String property, Object value);
}
