package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
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

@ManagedBean(name = "organizationalActivitiesView")
@Getter
@Setter
@SessionScoped
@ViewPath(path = HyperLinks.ORGANIZATIONAL_ACTIVITIES_VIEW)
public class OrganizationalActivitiesView implements Serializable {

    private static final long serialVersionUID = 1L;

    private ActivityService activityService;
    private OrganizationGoalService organizationGoalService;

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
    private String dataEmptyMessage = "No organizational activities found.";
    private Activity selectedActivity;

    @PostConstruct
    public void init() {
        activityService = ApplicationContextProvider.getBean(ActivityService.class);
        organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);

        this.recordStatusList = Arrays.asList(RecordStatus.values());
        this.activityStatusList = Arrays.asList(ActivityStatus.values());

        reloadFilterReset();
    }

    public void reloadFilterReset() {
        try {
            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterNotNull("organizationGoal");
            
            // Apply filters if set
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                search.addFilterLike("title", "%" + searchTerm.trim() + "%");
            }
            
            if (selectedStatus != null) {
                search.addFilterEqual("status", selectedStatus);
            }
            
            if (createdFrom != null) {
                search.addFilterGreaterOrEqual("dateCreated", createdFrom);
            }
            
            if (createdTo != null) {
                search.addFilterLessOrEqual("dateCreated", createdTo);
            }
            
            this.activityModels = activityService.getInstances(search, 0, 0);
            calculateStatistics();
            
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to load organizational activities: " + e.getMessage());
            this.activityModels = Arrays.asList();
        }
    }

    public void reloadFromDB() {
        try {
            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            search.addFilterNotNull("organizationGoal");
            
            // Apply filters if set
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                search.addFilterLike("title", "%" + searchTerm.trim() + "%");
            }
            
            if (selectedStatus != null) {
                search.addFilterEqual("status", selectedStatus);
            }
            
            if (createdFrom != null) {
                search.addFilterGreaterOrEqual("dateCreated", createdFrom);
            }
            
            if (createdTo != null) {
                search.addFilterLessOrEqual("dateCreated", createdTo);
            }
            
            this.activityModels = activityService.getInstances(search, 0, 0);
            calculateStatistics();
            
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to load organizational activities: " + e.getMessage());
            this.activityModels = Arrays.asList();
        }
    }

    private void calculateStatistics() {
        if (this.activityModels != null) {
            this.totalActivities = this.activityModels.size();
            this.activeActivities = (int) this.activityModels.stream()
                    .filter(a -> ActivityStatus.IN_PROGRESS.equals(a.getStatus()))
                    .count();
            this.pendingActivities = (int) this.activityModels.stream()
                    .filter(a -> ActivityStatus.PENDING.equals(a.getStatus()))
                    .count();
            this.completedActivities = (int) this.activityModels.stream()
                    .filter(a -> ActivityStatus.COMPLETED.equals(a.getStatus()))
                    .count();
        } else {
            this.totalActivities = 0;
            this.activeActivities = 0;
            this.pendingActivities = 0;
            this.completedActivities = 0;
        }
    }

    public void clearFilters() {
        this.searchTerm = null;
        this.selectedStatus = null;
        this.createdFrom = null;
        this.createdTo = null;
        reloadFilterReset();
    }

    public void deleteActivity(Activity activity) {
        try {
            if (activity != null) {
                activity.setRecordStatus(RecordStatus.DELETED);
                activityService.saveInstance(activity);
                UiUtils.showMessageBox("Success", "Activity deleted successfully.");
                reloadFilterReset();
            }
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to delete activity: " + e.getMessage());
        }
    }

    public void search() {
        reloadFromDB();
    }
}
