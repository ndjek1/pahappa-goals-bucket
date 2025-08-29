package org.pahappa.systems.kpiTracker.core.services.kpis;

import org.pahappa.systems.kpiTracker.core.services.GenericService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;

import java.util.List;

/**
 * Service interface for managing KPI update history
 */
public interface KpiUpdateHistoryService extends GenericService<KpiUpdateHistory> {
    
    /**
     * Get all update history for a specific KPI, ordered by update date descending
     * @param kpi The KPI to get history for
     * @return List of update history records
     */
    List<KpiUpdateHistory> getUpdateHistoryByKpi(KPI kpi);
    
    /**
     * Create a new update history record for a KPI
     * @param kpi The KPI being updated
     * @param previousValue The previous value before update
     * @param newValue The new value after update
     * @param comment Optional comment for the update
     * @return The saved update history record
     */
    KpiUpdateHistory createUpdateHistory(KPI kpi, Double previousValue, Double newValue, String comment);
    
    /**
     * Get the latest update for a specific KPI
     * @param kpi The KPI to get latest update for
     * @return The most recent update history record, or null if none exists
     */
    KpiUpdateHistory getLatestUpdateByKpi(KPI kpi);
    
    /**
     * Get update history for a KPI within a date range
     * @param kpi The KPI to get history for
     * @param fromDate Start date (inclusive)
     * @param toDate End date (inclusive)
     * @return List of update history records within the date range
     */
    List<KpiUpdateHistory> getUpdateHistoryByKpiAndDateRange(KPI kpi, java.util.Date fromDate, java.util.Date toDate);
}
