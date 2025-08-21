package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;

import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

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
@ViewPath(path = HyperLinks.ACTIVITIES_VIEW)
public class ActivitiesView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActivitiesView.class.getSimpleName());

    private ActivityService activityService;
    private OrganizationGoalService organizationGoalService;
    private DepartmentGoalService departmentGoalService;
    private TeamGoalService teamGoalService;

    private List<RecordStatus> recordStatusList;
    private List<ActivityStatus> activityStatusList;

    private int totalActivities;
    private int activeActivities;
    private int pendingActivities;
    private int completedActivities;

    private String searchTerm;
    private ActivityStatus selectedStatus;
    private Date createdFrom, createdTo;

    private List<Activity> activityModels;
    private String dataEmptyMessage = "No activities found.";
    private Activity selectedActivity;

    @PostConstruct
    public void init() {
        activityService = ApplicationContextProvider.getBean(ActivityService.class);
        organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);

        this.recordStatusList = Arrays.asList(RecordStatus.values());
        this.activityStatusList = Arrays.asList(ActivityStatus.values());

        reloadFilterReset();
    }

    public void reloadFilterReset() {
        try {
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
                activitySearch.addFilterEqual("status", selectedStatus);
            }
            if (createdFrom != null) {
                activitySearch.addFilterGreaterOrEqual("dateCreated", createdFrom);
            }
            if (createdTo != null) {
                activitySearch.addFilterLessOrEqual("dateCreated", createdTo);
            }

            // Filter and load dataModels based on search/filter criteria
            this.activityModels = activityService.getInstances(activitySearch, 0, 1000);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reloading activities", e);
            UiUtils.ComposeFailure("Error", "Failed to reload activities: " + e.getMessage());
        }
    }

    public void reloadFromDB(int offset, int limit, java.util.Map<String, Object> filters) throws Exception {
        try {
            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                search.addFilterILike("title", "%" + searchTerm + "%");
            }
            
            this.activityModels = activityService.getInstances(search, offset, limit);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error reloading activities from DB", e);
            throw e;
        }
    }

    public void deleteActivity(Activity activity) {
        try {
            if (activity != null) {
                activityService.deleteInstance(activity);
                UiUtils.showMessageBox("Action successful", "Activity has been deleted successfully.");
                reloadFilterReset();
            }
        } catch (Exception e) {
            UiUtils.ComposeFailure("Action failed", "Failed to delete activity: " + e.getMessage());
        }
    }
    
    public void clearFilters() {
        this.searchTerm = null;
        this.selectedStatus = null;
        this.createdFrom = null;
        this.createdTo = null;
        reloadFilterReset();
    }

    public List<ExcelReport> getExcelReportModels() {
        return null;
    }
}
