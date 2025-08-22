package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "organizationalKpiView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.ORGANIZATIONAL_KPIS_VIEW)
public class OrganizationalKPIView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrganizationalKPIView.class.getSimpleName());

    private KpisService kpisService;
    private List<KPI> dataModels;
    private String searchTerm;
    private Date createdFrom, createdTo;
    private String dataEmptyMessage = "No organizational KPIs found.";
    private KPI selectedKPI;
    
    // Filter properties
    private List<MeasurementUnit> measurementUnits;
    private List<Frequency> frequencies;
    private MeasurementUnit selectedMeasurementUnit;
    private Frequency selectedFrequency;

    // Statistics
    private int totalKpis;
    private int activeKpis;
    private int onTrackKpis;
    private int behindScheduleKpis;

    @PostConstruct
    public void init() {
        kpisService = ApplicationContextProvider.getBean(KpisService.class);
        
        // Initialize enum lists for filters
        this.measurementUnits = Arrays.asList(MeasurementUnit.values());
        this.frequencies = Arrays.asList(Frequency.values());
        
        reloadFilterReset();
    }

    public void reloadFilterReset() {
        try {
            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterNotNull("organizationGoal");
            
            // Apply filters if set
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                search.addFilterLike("name", "%" + searchTerm.trim() + "%");
            }
            
            if (selectedMeasurementUnit != null) {
                search.addFilterEqual("measurementUnit", selectedMeasurementUnit);
            }
            
            if (selectedFrequency != null) {
                search.addFilterEqual("frequency", selectedFrequency);
            }
            
            if (createdFrom != null) {
                search.addFilterGreaterOrEqual("dateCreated", createdFrom);
            }
            
            if (createdTo != null) {
                search.addFilterLessOrEqual("dateCreated", createdTo);
            }
            
            this.dataModels = kpisService.getInstances(search, 0, 0);
            calculateStatistics();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading organizational KPIs", e);
            UiUtils.ComposeFailure("Error", "Failed to load organizational KPIs: " + e.getMessage());
            this.dataModels = Arrays.asList();
        }
    }

    public void reloadFromDB() {
        try {
            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterNotNull("organizationGoal");
            
            // Apply filters if set
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                search.addFilterLike("name", "%" + searchTerm.trim() + "%");
            }
            
            if (selectedMeasurementUnit != null) {
                search.addFilterEqual("measurementUnit", selectedMeasurementUnit);
            }
            
            if (selectedFrequency != null) {
                search.addFilterEqual("frequency", selectedFrequency);
            }
            
            if (createdFrom != null) {
                search.addFilterGreaterOrEqual("dateCreated", createdFrom);
            }
            
            if (createdTo != null) {
                search.addFilterLessOrEqual("dateCreated", createdTo);
            }
            
            this.dataModels = kpisService.getInstances(search, 0, 0);
            calculateStatistics();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reloading organizational KPIs", e);
            UiUtils.ComposeFailure("Error", "Failed to reload organizational KPIs: " + e.getMessage());
            this.dataModels = Arrays.asList();
        }
    }

    private void calculateStatistics() {
        if (dataModels == null) {
            this.totalKpis = 0;
            this.activeKpis = 0;
            this.onTrackKpis = 0;
            this.behindScheduleKpis = 0;
            return;
        }

        this.totalKpis = dataModels.size();
        this.activeKpis = (int) dataModels.stream()
                .filter(k -> RecordStatus.ACTIVE.equals(k.getRecordStatus()))
                .count();
        
        this.onTrackKpis = (int) dataModels.stream()
                .filter(k -> k.getAccomplishmentPercentage() != null && k.getAccomplishmentPercentage() >= 80)
                .count();
        
        this.behindScheduleKpis = (int) dataModels.stream()
                .filter(k -> k.getAccomplishmentPercentage() != null && k.getAccomplishmentPercentage() < 50)
                .count();
    }

    public void clearFilters() {
        this.searchTerm = null;
        this.selectedMeasurementUnit = null;
        this.selectedFrequency = null;
        this.createdFrom = null;
        this.createdTo = null;
        reloadFilterReset();
    }

    public void search() {
        reloadFromDB();
    }

    public void deleteSelectedKPI(KPI kpi) {
        try {
            if (kpi != null) {
                kpi.setRecordStatus(RecordStatus.DELETED);
                kpisService.saveInstance(kpi);
                UiUtils.showMessageBox("Success", "KPI deleted successfully.");
                reloadFilterReset();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting KPI", e);
            UiUtils.ComposeFailure("Error", "Failed to delete KPI: " + e.getMessage());
        }
    }
}
