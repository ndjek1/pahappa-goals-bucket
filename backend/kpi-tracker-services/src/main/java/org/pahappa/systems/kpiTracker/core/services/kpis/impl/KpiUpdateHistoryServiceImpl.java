package org.pahappa.systems.kpiTracker.core.services.kpis.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.core.services.impl.GenericServiceImpl;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class KpiUpdateHistoryServiceImpl extends GenericServiceImpl<KpiUpdateHistory> implements KpiUpdateHistoryService {

    @Override
    public KpiUpdateHistory saveInstance(KpiUpdateHistory entityInstance) throws ValidationFailedException, OperationFailedException {
        Validate.notNull(entityInstance, "KPI Update History details missing");
        Validate.notNull(entityInstance.getKpi(), "KPI reference missing");
        Validate.notNull(entityInstance.getNewValue(), "New value is required");
        Validate.notNull(entityInstance.getUpdateDate(), "Update date is required");
        
        return save(entityInstance);
    }

    @Override
    public boolean isDeletable(KpiUpdateHistory instance) throws OperationFailedException {
        // Generally, we don't want to delete update history as it's audit data
        return false;
    }

    @Override
    public List<KpiUpdateHistory> getUpdateHistoryByKpi(KPI kpi) {
        try {
            Validate.notNull(kpi, "KPI cannot be null");
        } catch (ValidationFailedException e) {
            throw new RuntimeException("Invalid KPI parameter", e);
        }
        
        Search search = new Search();
        search.addFilterEqual("kpi", kpi);
        search.addSortDesc("updateDate");
        
        return super.search(search);
    }

    @Override
    public KpiUpdateHistory createUpdateHistory(KPI kpi, Double previousValue, Double newValue, String comment) {
        try {
            Validate.notNull(kpi, "KPI cannot be null");
            Validate.notNull(newValue, "New value cannot be null");
        } catch (ValidationFailedException e) {
            throw new RuntimeException("Invalid parameters", e);
        }
        
        try {
            KpiUpdateHistory updateHistory = new KpiUpdateHistory();
            updateHistory.setKpi(kpi);
            updateHistory.setPreviousValue(previousValue);
            updateHistory.setNewValue(newValue);
            updateHistory.setUpdateComment(comment);
            updateHistory.setUpdateDate(new Date());
            
            // Calculate accomplishment percentage
            if (kpi.getTargetValue() != null && kpi.getTargetValue() > 0) {
                Double percentage = (newValue * 100.0) / kpi.getTargetValue();
                updateHistory.setAccomplishmentPercentage(percentage);
            }
            
            // Set the user who made the update
            User loggedInUser = SharedAppData.getLoggedInUser();
            updateHistory.setUpdatedByUser(loggedInUser);
            
            // Try to get staff record for the logged-in user
            try {
                StaffService staffService = ApplicationContextProvider.getBean(StaffService.class);
                Search staffSearch = new Search();
                staffSearch.addFilterEqual("user", loggedInUser);
                List<Staff> staffList = staffService.getAllInstances(); // Use getAllInstances instead
                // Filter manually since search might not be available
                for (Staff staff : staffList) {
                    if (staff.getUser() != null && staff.getUser().equals(loggedInUser)) {
                        updateHistory.setUpdatedByStaff(staff);
                        break;
                    }
                }
            } catch (Exception e) {
                // Continue without staff reference if not found
            }
            
            return saveInstance(updateHistory);
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating KPI update history", e);
        }
    }

    @Override
    public KpiUpdateHistory getLatestUpdateByKpi(KPI kpi) {
        try {
            Validate.notNull(kpi, "KPI cannot be null");
        } catch (ValidationFailedException e) {
            throw new RuntimeException("Invalid KPI parameter", e);
        }
        
        Search search = new Search();
        search.addFilterEqual("kpi", kpi);
        search.addSortDesc("updateDate");
        search.setMaxResults(1);
        
        List<KpiUpdateHistory> results = super.search(search);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<KpiUpdateHistory> getUpdateHistoryByKpiAndDateRange(KPI kpi, Date fromDate, Date toDate) {
        try {
            Validate.notNull(kpi, "KPI cannot be null");
        } catch (ValidationFailedException e) {
            throw new RuntimeException("Invalid KPI parameter", e);
        }
        
        Search search = new Search();
        search.addFilterEqual("kpi", kpi);
        
        if (fromDate != null) {
            search.addFilterGreaterOrEqual("updateDate", fromDate);
        }
        
        if (toDate != null) {
            search.addFilterLessOrEqual("updateDate", toDate);
        }
        
        search.addSortDesc("updateDate");
        
        return super.search(search);
    }
}
