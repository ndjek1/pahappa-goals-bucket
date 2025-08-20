package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
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

@ManagedBean(name = "activitiesView")
@Getter
@Setter
@SessionScoped
public class ActivitiesView implements Serializable {

    private static final long serialVersionUID = 1L;

    private ActivityService activityService;
    private OrganizationGoalService organizationGoalService;
    private DepartmentGoalService departmentGoalService;
    private TeamGoalService teamGoalService;

    private List<RecordStatus> recordStatusList;

    private int totalActivities;
    private int activeActivities;
    private int pendingActivities;
    private int completedActivities;

    private String searchTerm;
    private RecordStatus selectedStatus;
    private Date createdFrom, createdTo;

    private List<Activity> activityModels;
    private String dataEmptyMessage = "No activities found.";

    @PostConstruct
    public void init() {
        activityService = ApplicationContextProvider.getBean(ActivityService.class);
        organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);

        this.recordStatusList = Arrays.asList(RecordStatus.values());

        reloadFilterReset();
    }

    public void reloadFilterReset() {
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

        // Filter and load dataModels based on search/filter criteria
        this.activityModels = activityService.getInstances(activitySearch, 0, 1000);
    }

    public void reloadFromDB(int i, int i1, java.util.Map<String, Object> map) throws Exception {
        Search search = new Search();
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("title", "%" + searchTerm + "%");
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
