package org.pahappa.systems.kpiTracker.core.services.kpis.impl;

import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KpisServiceImpl extends GenericServiceImpl<KPI> implements KpisService {

    @Autowired
    KpiUpdateHistoryService kpiUpdateHistoryService;
    @Override
    public KPI saveInstance(KPI entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "Missing details");
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(KPI instance) throws OperationFailedException {
        return true;
    }

    @Override
    public Object getObjectById(String id) {
        return super.getInstanceByID(id);
    }

    @Override
    public double getKpiProgress(KPI kpi) {
        if (kpi == null) return 0.0;
        if (kpi.getTargetValue() == null || kpi.getTargetValue() <= 0) return 0.0;

        double currentValue = 0.0;

        try {
            List<KpiUpdateHistory> updates = kpiUpdateHistoryService.getUpdateHistoryByKpi(kpi);
            if (updates != null && !updates.isEmpty()) {
                for (KpiUpdateHistory update : updates) {
                    if (update.getValue() != null) {
                        currentValue += update.getValue();
                    }
                }
            }
        } catch (Exception ex) {
            // If fetching history fails, fall back to kpi.getCurrentValue()
            if (kpi.getCurrentValue() != null) {
                currentValue = kpi.getCurrentValue();
            }
        }

        if (currentValue <= 0) return 0.0;

        double frac = currentValue / kpi.getTargetValue(); // fraction (0.0–1.0)
        if (Double.isNaN(frac) || Double.isInfinite(frac)) frac = 0.0;

        // clamp between 0 and 1
        if (frac < 0) frac = 0;
        if (frac > 1) frac = 1;

        return frac;
    }

}
