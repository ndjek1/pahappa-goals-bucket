package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
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

@ManagedBean(name = "departmentKPIView")
@Getter
@Setter
@SessionScoped
public class DepartmentKPIView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private KpisService kpisService;
    private DepartmentService departmentService;
    private ReviewCycleService reviewCycleService;
    private Department department;
    private User loggedinUser;
    private List<KPI> dataModels;
    private String searchTerm;
    private Date createdFrom, createdTo;
    private String dataEmptyMessage = "No department KPIs found.";
    
    // Filter properties
    private List<MeasurementUnit> measurementUnitList;
    private List<Frequency> frequencyList;
    private List<ReviewCycle> reviewCycles;
    private MeasurementUnit selectedMeasurementUnit;
    private Frequency selectedFrequency;
    private ReviewCycle selectedReviewCycle;

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        
        // Initialize enum lists for filters
        this.measurementUnitList = Arrays.asList(MeasurementUnit.values());
        this.frequencyList = Arrays.asList(Frequency.values());
        
        loadDepartment();
        reloadFilterReset();
    }

    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        try {
            if (department != null) {
                Search search1 = new Search();

                // Create AND filter for department goal and recordStatus - more defensive approach
                search1.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
                search1.addFilter(Filter.and(
                    Filter.isNotNull("departmentGoal"),
                    Filter.equal("departmentGoal.department", department)
                ));
                
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

                this.dataModels = kpisService.getInstances(search1, i, i1);
            } else {
                this.dataModels = Arrays.asList(); // Empty list if no department
            }
        } catch (Exception e) {
            System.err.println("Error in reloadFromDB: " + e.getMessage());
            e.printStackTrace();
            this.dataModels = Arrays.asList(); // Return empty list on error
            throw e; // Re-throw for proper error handling
        }
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
        try {
            if (department != null) {
                Search search = new Search();
                search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
                
                // Try to filter by department - using a more defensive approach
                search.addFilter(Filter.and(
                    Filter.isNotNull("departmentGoal"),
                    Filter.equal("departmentGoal.department", department)
                ));
                
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
            } else {
                this.dataModels = Arrays.asList(); // Empty list if no department
            }
        } catch (Exception e) {
            System.err.println("Error loading department KPIs: " + e.getMessage());
            e.printStackTrace();
            this.dataModels = Arrays.asList(); // Return empty list on error
        }
    }

    public void loadDepartment() {
        if (loggedinUser.hasRole("DEPT_LEAD")) {
            this.department = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null
                            && d.getDepartmentHead().equals(loggedinUser))
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
        this.createdTo = null;
        reloadFilterReset();
    }
    
    public void viewKPI(KPI kpi) {
        // This method can be implemented to show KPI details
        // For now, it's a placeholder that could be expanded
        System.out.println("Viewing KPI: " + (kpi != null ? kpi.getName() : "null"));
    }
}
