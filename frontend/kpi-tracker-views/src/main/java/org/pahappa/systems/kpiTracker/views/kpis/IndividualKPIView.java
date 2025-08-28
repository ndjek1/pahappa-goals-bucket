package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.MeasurementUnit;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean(name = "individualKPIView")
@Getter
@Setter
@SessionScoped
public class IndividualKPIView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(IndividualKPIView.class.getSimpleName());
    
    private KpisService kpisService;
    private IndividualGoalService individualGoalService;
    private StaffService staffService;
    private ReviewCycleService reviewCycleService;
    private Staff staff;
    private User loggedinUser;
    private List<KPI> dataModels;
    private String searchTerm;
    private Date createdFrom, createdTo;
    private String dataEmptyMessage = "No individual KPIs found.";
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
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.loggedinUser = SharedAppData.getLoggedInUser();
        
        // Initialize enum lists for filters
        this.measurementUnits = Arrays.asList(MeasurementUnit.values());
        this.frequencies = Arrays.asList(Frequency.values());
        
        // Load review cycles
        loadReviewCycles();
        
        loadStaff();
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

    public void reloadFromDB(int offset, int limit, Map<String, Object> filters) throws Exception {
        Search search = new Search();
        
        // Create AND filter for individual goal and recordStatus
        Filter filter = Filter.and(
                Filter.equal("individualGoal.staff", staff),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );
        
        search.addFilter(filter);
        
        // Add search term filter
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
        
        // Add review cycle filter
        if (selectedReviewCycle != null) {
            search.addFilterEqual("reviewCycle", selectedReviewCycle);
        }
        
        // Add date filters
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

    public List load(int first, int pageSize, Map sortBy, Map filterBy) {
        return getDataModels();
    }

    public void reloadFilterReset() {
        try {
            if (staff != null) {
                Search search = new Search();
                search.addFilterEqual("individualGoal.staff", staff);
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
                
                // Add date filters
                if (createdFrom != null) {
                    search.addFilterGreaterOrEqual("dateCreated", createdFrom);
                }
                
                if (createdTo != null) {
                    search.addFilterLessOrEqual("dateCreated", createdTo);
                }
                
                this.dataModels = kpisService.getInstances(search, 0, 1000);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reloading individual KPIs", e);
        }
    }

    public void loadStaff() {
        try {
            // Get staff record for the logged-in user
            this.staff = staffService.getAllInstances()
                    .stream()
                    .filter(s -> s.getUser() != null && s.getUser().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading staff information", e);
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
    
    public void deleteSelectedKPI(KPI kpi) {
        try {
            if (kpi != null) {
                kpisService.deleteInstance(kpi);
                reloadFilterReset();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting KPI", e);
        }
    }
    
    public void viewKPI(KPI kpi) {
        // This method can be implemented to show KPI details
        // For now, it's a placeholder that could be expanded
        System.out.println("Viewing KPI: " + (kpi != null ? kpi.getName() : "null"));
    }
}