package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.DepartmentActivityService;
import org.pahappa.systems.kpiTracker.core.services.activities.TeamActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityPriority;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityType;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "teamActivityFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class TeamActivityFormDialog extends DialogForm<TeamActivity> {

    private static final long serialVersionUID = 1L;

    private TeamActivityService teamActivityService;
    private TeamGoalService teamGoalService;
    private UserService userService;
    private TeamService teamService;

    private List<TeamGoal> teamGoals;
    private List<ActivityStatus> activityStatuses;
    private List<User> users;
    private List<ActivityType> activityTypes;
    private List<ActivityPriority> priorities;

    private TeamGoal teamGoal;
    private ActivityStatus selectedStatus;
    private ActivityType selectedActivityType;
    private ActivityPriority selectedPriority;
    private Team team;
    private User loggedinUser;


    // Add edit field like user dialogs
    private boolean edit;

    public TeamActivityFormDialog() {
        super(HyperLinks.TEAM_ACTIVITY_FORM_DIALOG, 700, 400);
    }

    @PostConstruct
    public void init() {
        this.teamActivityService = ApplicationContextProvider.getBean(TeamActivityService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.loggedinUser = SharedAppData.getLoggedInUser();
        loadData();
        resetModal();
        loadDepartment();
    }

    private void loadData() {
        try {
            this.teamGoals = teamGoalService.getAllInstances();
        } catch (Exception e) {
            this.teamGoals = Arrays.asList();
        }

        try {
            this.activityStatuses = Arrays.asList(ActivityStatus.values());
        } catch (Exception e) {
            this.activityStatuses = Arrays.asList();
        }

        try {
            this.users = userService.getUsers();
        } catch (Exception e) {
            this.users = Arrays.asList();
        }

        // Initialize activity types and priorities
        this.activityTypes = Arrays.asList(ActivityType.values());
        this.priorities = Arrays.asList(ActivityPriority.values());

    }

    public void loadDepartment() {
        if (this.loggedinUser != null && this.loggedinUser.hasRole("Team Lead")) {
            this.team = teamService.getAllInstances()
                    .stream()
                    .filter(d -> d.getTeamHead() != null && d.getTeamHead().equals(this.loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }
    public boolean isQuantitativeSelected() {
        return selectedActivityType == ActivityType.QUANTITATIVE;
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (model.getTitle() == null || model.getTitle().trim().isEmpty()) {
                UiUtils.showMessageBox("Missing title", "Activity must have a title.");
                return;
            }

            // Require target value if quantitative
            if (selectedActivityType == ActivityType.QUANTITATIVE && model.getTargetValue() <= 0) {
                UiUtils.ComposeFailure("Validation Error", "Target value is required for quantitative activities.");
                return;
            }

            if (teamGoal != null) model.setTeamGoal(teamGoal);
            if (selectedStatus != null)        model.setStatus(selectedStatus);
            if (team != null)            model.setTeam(team);
            if (selectedActivityType != null)  model.setActivityType(selectedActivityType);
            if (selectedPriority != null)      model.setPriority(selectedPriority);

            teamActivityService.saveInstance(model);
            resetModal();
            hide();
        } catch (ValidationFailedException | OperationFailedException e) {
            UiUtils.ComposeFailure("Error", e.getMessage());
            throw e;
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to save team activity: " + e.getMessage());
            throw new OperationFailedException("Failed to save team activity: " + e.getMessage(), e);
        }
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new TeamActivity();
        setEdit(false);
        clearSelections();
    }


    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (this.model != null && this.model.getId() != null) {
            this.edit = true;
            // Set selections based on existing model
            if (model.getTeamGoal() != null) {
                teamGoal = model.getTeamGoal();
            }
            selectedStatus = model.getStatus();
            selectedActivityType = model.getActivityType();
            selectedPriority = model.getPriority();
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        teamGoal = null;
        selectedStatus = null;
        selectedActivityType = null;
        selectedPriority = null;
    }

    public void show() {
        // Show the dialog using the DialogForm base class method
        super.show(null);
    }

    public void hide() {
        // Hide the dialog using the DialogForm base class method
        super.hide();
    }
}
