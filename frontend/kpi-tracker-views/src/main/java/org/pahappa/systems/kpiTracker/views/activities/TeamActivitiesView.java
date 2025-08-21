package org.pahappa.systems.kpiTracker.views.activities;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.security.UiUtils;
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

@ManagedBean(name = "teamActivitiesView")
@Getter
@Setter
@SessionScoped
public class TeamActivitiesView implements Serializable {

    private static final long serialVersionUID = 1L;

    private ActivityService activityService;
    private TeamService teamService;
    private TeamGoalService teamGoalService;
    private UserService userService;

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
    private Team currentTeam;
    private String dataEmptyMessage = "No team activities found.";
    private Activity selectedActivity;

    @PostConstruct
    public void init() {
        activityService = ApplicationContextProvider.getBean(ActivityService.class);
        teamService = ApplicationContextProvider.getBean(TeamService.class);
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);

        this.recordStatusList = Arrays.asList(RecordStatus.values());
        this.activityStatusList = Arrays.asList(ActivityStatus.values());

        loadTeam();
        reloadFilterReset();
    }

    private void loadTeam() {
        // Load team based on logged-in user's team
        // This would typically be based on user role and team assignment
        try {
            // For now, load the first available team
            // In a real implementation, this would be based on user context
            List<Team> teams = teamService.getAllInstances();
            if (!teams.isEmpty()) {
                this.currentTeam = teams.get(0);
            }
        } catch (Exception e) {
            // Initialize with empty team if service fails
            this.currentTeam = null;
        }
    }

    public void reloadFilterReset() {
        try {
            // Initialize with empty lists if no team is available
            if (currentTeam == null) {
                this.totalActivities = 0;
                this.activeActivities = 0;
                this.pendingActivities = 0;
                this.completedActivities = 0;
                this.activityModels = Arrays.asList();
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

            // Search for activities related to team goals
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

            // Filter activities by team goal
            Search teamGoalSearch = new Search();
            teamGoalSearch.addFilterEqual("team", currentTeam);
            List<TeamGoal> teamGoals = teamGoalService.getInstances(teamGoalSearch, 0, 1000);
            if (!teamGoals.isEmpty()) {
                activitySearch.addFilterIn("teamGoal", teamGoals);
            }

            // Filter and load dataModels based on search/filter criteria
            this.activityModels = activityService.getInstances(activitySearch, 0, 1000);
        } catch (Exception e) {
            // Initialize with empty values if any service fails
            this.totalActivities = 0;
            this.activeActivities = 0;
            this.pendingActivities = 0;
            this.completedActivities = 0;
            this.activityModels = Arrays.asList();
        }
    }

    public void reloadFromDB(int i, int i1, java.util.Map<String, Object> map) throws Exception {
        try {
            if (currentTeam == null) {
                this.activityModels = Arrays.asList();
                return;
            }

            Search search = new Search();
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            
            if (searchTerm != null && !searchTerm.isEmpty()) {
                search.addFilterILike("title", "%" + searchTerm + "%");
            }
            
            // Filter activities by team goal
            Search teamGoalSearch = new Search();
            teamGoalSearch.addFilterEqual("team", currentTeam);
            List<TeamGoal> teamGoals = teamGoalService.getInstances(teamGoalSearch, 0, 1000);
            if (!teamGoals.isEmpty()) {
                search.addFilterIn("teamGoal", teamGoals);
            }
            
            this.activityModels = activityService.getInstances(search, i, i1);
        } catch (Exception e) {
            this.activityModels = Arrays.asList();
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
