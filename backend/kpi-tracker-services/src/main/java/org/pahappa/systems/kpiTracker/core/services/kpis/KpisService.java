package org.pahappa.systems.kpiTracker.core.services.kpis;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;

public interface KpisService extends GenericService<KPI> {
    Object getObjectById(String var1);
    double getKpiProgress(KPI kpi);
}
