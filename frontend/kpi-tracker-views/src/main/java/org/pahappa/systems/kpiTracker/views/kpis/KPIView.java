package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "kpiView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.KPIS_VIEW)
public class KPIView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(KPIView.class.getSimpleName());

    private KpisService kpisService;
    private ReviewCycleService reviewCycleService;
    private List<KPI> dataModels;
    private String searchTerm;
    private Date createdFrom, createdTo;
    private String dataEmptyMessage = "No KPIs found.";
    private KPI selectedKPI;
    
    // Filter properties
    private List<MeasurementUnit> measurementUnits;
    private List<Frequency> frequencies;
    private List<ReviewCycle> reviewCycles;
    private MeasurementUnit selectedMeasurementUnit;
    private Frequency selectedFrequency;
    private ReviewCycle selectedReviewCycle;

    @PostConstruct
    public void init() {
        kpisService = ApplicationContextProvider.getBean(KpisService.class);
        reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        
        // Initialize enum lists for filters
        this.measurementUnits = Arrays.asList(MeasurementUnit.values());
        this.frequencies = Arrays.asList(Frequency.values());
        
        // Load review cycles
        loadReviewCycles();
        
        reloadFilterReset();
    }

    private void loadReviewCycles() {
        try {
            this.reviewCycles = reviewCycleService.getAllInstances();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading review cycles", e);
            this.reviewCycles = new ArrayList<>();
        }
    }

    public void reloadFilterReset() {
        try {
            Search kpiSearch = new Search();
            kpiSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                kpiSearch.addFilterILike("name", "%" + searchTerm + "%");
            }
            
            // Add measurement unit filter
            if (selectedMeasurementUnit != null) {
                kpiSearch.addFilterEqual("measurementUnit", selectedMeasurementUnit);
            }
            
            // Add frequency filter
            if (selectedFrequency != null) {
                kpiSearch.addFilterEqual("frequency", selectedFrequency);
            }
            
            // Add review cycle filter
            if (selectedReviewCycle != null) {
                kpiSearch.addFilterEqual("reviewCycle", selectedReviewCycle);
            }
            
            if (createdFrom != null) {
                kpiSearch.addFilterGreaterOrEqual("dateCreated", createdFrom);
            }
            
            if (createdTo != null) {
                kpiSearch.addFilterLessOrEqual("dateCreated", createdTo);
            }
            
            // Filter and load dataModels based on search/filter criteria
            this.dataModels = kpisService.getInstances(kpiSearch, 0, 1000);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reloading KPIs", e);
            UiUtils.ComposeFailure("Error", "Failed to reload KPIs: " + e.getMessage());
        }
    }

    public void reloadFromDB(int offset, int limit, java.util.Map<String, Object> filters) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("name", "%" + searchTerm + "%");
        }
        
        // Add measurement unit filter
        if (selectedMeasurementUnit != null) {
            search.addFilterEqual("measurementUnit", selectedMeasurementUnit);
        }
        
        // Add frequency filter
        if (selectedFrequency != null) {
            search.addFilterEqual("frequency", selectedFrequency);
        }
        
        if (createdFrom != null) {
            search.addFilterGreaterOrEqual("dateCreated", createdFrom);
        }
        
        if (createdTo != null) {
            search.addFilterLessOrEqual("dateCreated", createdTo);
        }
        
        this.dataModels = kpisService.getInstances(search, offset, limit);
    }

    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    public String getFileName() {
        return null;
    }

    public List load(int first, int pageSize, java.util.Map sortBy, java.util.Map filterBy) {
        return getDataModels();
    }

    public void deleteSelectedKPI(KPI kpi) {
        try {
            if (kpi != null) {
                kpisService.deleteInstance(kpi);
                UiUtils.showMessageBox("Action successful", "KPI has been deleted successfully.");
                reloadFilterReset();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting KPI", e);
            UiUtils.ComposeFailure("Action failed", "Failed to delete KPI: " + e.getMessage());
        }
    }

    public void clearFilters() {
        this.searchTerm = null;
        this.selectedMeasurementUnit = null;
        this.selectedFrequency = null;
        this.selectedReviewCycle = null;
        this.createdFrom = null;
        this.createdTo = null;
        reloadFilterReset();
    }
    
    public void viewKPI(KPI kpi) {
        // This method can be implemented to show KPI details
        // For now, it's a placeholder that could be expanded
        System.out.println("Viewing KPI: " + (kpi != null ? kpi.getName() : "null"));
    }
    
    public String viewKpiDetails(KPI kpi) {
        this.selectedKPI = kpi;
        return HyperLinks.KPI_DETAIL_VIEW + "?kpiId=" + kpi.getId();
    }
}
