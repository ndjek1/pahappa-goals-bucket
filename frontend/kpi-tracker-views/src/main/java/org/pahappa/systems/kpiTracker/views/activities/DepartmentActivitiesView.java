package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "departmentActivitiesView")
@Getter
@Setter
@SessionScoped
public class DepartmentActivitiesView implements Serializable {

    private static final long serialVersionUID = 1L;

    private ActivityService activityService;
    private DepartmentService departmentService;
    private DepartmentGoalService departmentGoalService;
    private UserService userService;

    private List<RecordStatus> recordStatusList;

    private int totalActivities;
    private int activeActivities;
    private int pendingActivities;
    private int completedActivities;

    private String searchTerm;
    private RecordStatus selectedStatus;
    private Date createdFrom, createdTo;

    private List<Activity> activityModels;
    private Department currentDepartment;
    private String dataEmptyMessage = "No department activities found.";

    @PostConstruct
    public void init() {
        activityService = ApplicationContextProvider.getBean(ActivityService.class);
        departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);

        this.recordStatusList = Arrays.asList(RecordStatus.values());

        loadDepartment();
        reloadFilterReset();
    }

    private void loadDepartment() {
        // Load department based on logged-in user's department
        // This would typically be based on user role and department assignment
        try {
            // For now, load the first available department
            // In a real implementation, this would be based on user context
            List<Department> departments = departmentService.getAllInstances();
            if (!departments.isEmpty()) {
                this.currentDepartment = departments.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reloadFilterReset() {
        if (currentDepartment == null) {
            return;
        }

        Search allActivitiesSearch = new Search();
        this.totalActivities = activityService.countInstances(allActivitiesSearch);

        Search activeActivitiesSearch = new Search();
        activeActivitiesSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.activeActivities = activityService.countInstances(activeActivitiesSearch);

        Search pendingActivitiesSearch = new Search();
        pendingActivitiesSearch.addFilterEqual("status", ActivityStatus.PENDING);
        this.pendingActivities = activityService.countInstances(pendingActivitiesSearch);

        Search completedActivitiesSearch = new Search();
        completedActivitiesSearch.addFilterEqual("status", ActivityStatus.COMPLETED);
        this.completedActivities = activityService.countInstances(completedActivitiesSearch);

        // Search for activities related to department goals
        Search activitySearch = new Search();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            activitySearch.addFilterILike("title", "%" + searchTerm + "%");
        }
        if (selectedStatus != null) {
            activitySearch.addFilterEqual("recordStatus", selectedStatus);
        }
        if (createdFrom != null) {
            activitySearch.addFilterGreaterOrEqual("dateCreated", createdFrom);
        }
        if (createdTo != null) {
            activitySearch.addFilterLessOrEqual("dateCreated", createdTo);
        }

        // Filter activities by department goal
        Search deptGoalSearch = new Search();
        deptGoalSearch.addFilterEqual("department", currentDepartment);
        List<DepartmentGoal> departmentGoals = departmentGoalService.getInstances(deptGoalSearch, 0, 1000);
        if (!departmentGoals.isEmpty()) {
            activitySearch.addFilterIn("departmentGoal", departmentGoals);
        }

        // Filter and load dataModels based on search/filter criteria
        this.activityModels = activityService.getInstances(activitySearch, 0, 1000);
    }

    public void reloadFromDB(int i, int i1, java.util.Map<String, Object> map) throws Exception {
        if (currentDepartment == null) {
            return;
        }

        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("title", "%" + searchTerm + "%");
        }
        
        // Filter activities by department goal
        Search deptGoalSearch = new Search();
        deptGoalSearch.addFilterEqual("department", currentDepartment);
        List<DepartmentGoal> departmentGoals = departmentGoalService.getInstances(deptGoalSearch, 0, 1000);
        if (!departmentGoals.isEmpty()) {
            search.addFilterIn("departmentGoal", departmentGoals);
        }
        
        this.activityModels = activityService.getInstances(search, i, i1);
    }

    public void deleteActivity(Activity activity) {
        try {
            activityService.deleteInstance(activity);
            reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ExcelReport> getExcelReportModels() {
        return null;
    }
}
