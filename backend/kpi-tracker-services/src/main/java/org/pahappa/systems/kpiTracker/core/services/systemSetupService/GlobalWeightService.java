package org.pahappa.systems.kpiTracker.core.services.systemSetupService;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;

public interface GlobalWeightService extends GenericService<GlobalWeight> {
    GlobalWeight mergeBG(GlobalWeight entity);
    GlobalWeight searchUniqueByPropertyEqual(String property, Object value);
}
