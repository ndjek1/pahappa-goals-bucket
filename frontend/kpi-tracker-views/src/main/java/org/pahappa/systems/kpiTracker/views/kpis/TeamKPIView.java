package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
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

@ManagedBean(name = "teamKPIView")
@Getter
@Setter
@SessionScoped
public class TeamKPIView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private KpisService kpisService;
    private TeamService teamService;
    private ReviewCycleService reviewCycleService;
    private Team team;
    private User loggedinUser;
    private List<KPI> dataModels;
    private String searchTerm;
    private String dataEmptyMessage = "No team KPIs found.";
    
    // Filter properties
    private List<MeasurementUnit> measurementUnitList;
    private List<Frequency> frequencyList;
    private List<ReviewCycle> reviewCycles;
    private MeasurementUnit selectedMeasurementUnit;
    private Frequency selectedFrequency;
    private ReviewCycle selectedReviewCycle;
    private Date createdFrom;

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        
        // Initialize enum lists for filters
        this.measurementUnitList = Arrays.asList(MeasurementUnit.values());
        this.frequencyList = Arrays.asList(Frequency.values());
        
        // Load review cycles
        loadReviewCycles();
        
        loadTeam();
        reloadFilterReset();
    }

    private void loadReviewCycles() {
        try {
            this.reviewCycles = reviewCycleService.getAllInstances();
        } catch (Exception e) {
            this.reviewCycles = new ArrayList<>();
        }
    }

    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        Search search1 = new Search();

        // Create AND filter for team goal and recordStatus
        Filter filter = Filter.and(
                Filter.equal("teamGoal.team", team),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );

        search1.addFilter(filter);
        
        // Add measurement unit filter
        if (selectedMeasurementUnit != null) {
            search1.addFilterEqual("measurementUnit", selectedMeasurementUnit);
        }
        
        // Add frequency filter
        if (selectedFrequency != null) {
            search1.addFilterEqual("frequency", selectedFrequency);
        }
        
        // Add review cycle filter
        if (selectedReviewCycle != null) {
            search1.addFilterEqual("reviewCycle", selectedReviewCycle);
        }
        
        // Add date filter - defensive approach using available date fields
        if (createdFrom != null) {
            try {
                // Try to filter by startDate if createdFrom is specified
                search1.addFilterGreaterOrEqual("startDate", createdFrom);
            } catch (Exception e) {
                // If startDate doesn't work, we'll silently continue without this filter
                System.out.println("Date filter not applied: " + e.getMessage());
            }
        }

        this.dataModels = kpisService.getInstances(search1, i, i1);
    }

    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    public String getFileName() {
        return null;
    }

    public List load(int i, int i1, Map map, Map map1) {
        return null;
    }

    public void reloadFilterReset() {
        if (team != null) {
            Search search = new Search();
            search.addFilterEqual("teamGoal.team", team);
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
            
            // Add date filter - defensive approach using available date fields
            if (createdFrom != null) {
                try {
                    // Try to filter by startDate if createdFrom is specified
                    search.addFilterGreaterOrEqual("startDate", createdFrom);
                } catch (Exception e) {
                    // If startDate doesn't work, we'll silently continue without this filter
                    System.out.println("Date filter not applied: " + e.getMessage());
                }
            }
            
            this.dataModels = kpisService.getInstances(search, 0, 1000);
        }
    }

    public void loadTeam() {
        if (loggedinUser.hasRole("Team Lead")) {
            this.team = teamService.getAllInstances()
                    .stream()
                    .filter(t -> t.getTeamHead() != null
                            && t.getTeamHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }
    
    public void clearFilters() {
        this.searchTerm = null;
        this.selectedMeasurementUnit = null;
        this.selectedFrequency = null;
        this.selectedReviewCycle = null;
        this.createdFrom = null;
        reloadFilterReset();
    }
    
    public void viewKPI(KPI kpi) {
        // This method can be implemented to show KPI details
        // For now, it's a placeholder that could be expanded
        System.out.println("Viewing KPI: " + (kpi != null ? kpi.getName() : "null"));
    }
}
