package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
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
import java.util.Date;
import java.util.List;

@ManagedBean(name = "activityFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class ActivityFormDialog extends DialogForm<Activity> {

    private static final long serialVersionUID = 1L;
    
    private ActivityService activityService;
    private OrganizationGoalService organizationGoalService;
    private DepartmentGoalService departmentGoalService;
    private TeamGoalService teamGoalService;
    private UserService userService;
    
    private List<OrganizationGoal> organizationGoals;
    private List<DepartmentGoal> departmentGoals;
    private List<TeamGoal> teamGoals;
    private List<ActivityStatus> activityStatuses;
    private List<User> users;
    
    private OrganizationGoal selectedOrganizationGoal;
    private DepartmentGoal selectedDepartmentGoal;
    private TeamGoal selectedTeamGoal;
    private User selectedUser;
    private ActivityStatus selectedStatus;
    
    private boolean edit;

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        
        loadData();
    }

    public ActivityFormDialog() {
        super(HyperLinks.ACTIVITY_FORM_DIALOG, 700, 600);
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        if (super.model.getTitle() == null || super.model.getTitle().trim().isEmpty()) {
            UiUtils.showMessageBox("Missing title", "Activity must have a title.");
            return;
        }
        
        if (selectedUser == null) {
            UiUtils.showMessageBox("Missing user", "Activity must be assigned to a user.");
            return;
        }
        
        // Set the selected user
        super.model.setUser(selectedUser);
        
        // Set the selected goal (only one can be selected)
        if (selectedOrganizationGoal != null) {
            super.model.setOrganizationGoal(selectedOrganizationGoal);
        } else if (selectedDepartmentGoal != null) {
            super.model.setDepartmentGoal(selectedDepartmentGoal);
        } else if (selectedTeamGoal != null) {
            super.model.setTeamGoal(selectedTeamGoal);
        }
        
        // Set status if selected
        if (selectedStatus != null) {
            super.model.setStatus(selectedStatus);
        }
        
        try {
            activityService.saveInstance(super.model);
        } catch (Exception e) {
            UiUtils.showMessageBox("Error", "Failed to save activity: " + e.getMessage());
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Activity();
        setEdit(false);
        clearSelections();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null) {
            setEdit(true);
            // Set selections based on existing model
            if (super.model.getOrganizationGoal() != null) {
                selectedOrganizationGoal = super.model.getOrganizationGoal();
            } else if (super.model.getDepartmentGoal() != null) {
                selectedDepartmentGoal = super.model.getDepartmentGoal();
            } else if (super.model.getTeamGoal() != null) {
                selectedTeamGoal = super.model.getTeamGoal();
            }
            selectedUser = super.model.getUser();
            selectedStatus = super.model.getStatus();
        }
    }

    private void loadData() {
        try {
            this.organizationGoals = organizationGoalService.getAllInstances();
            this.departmentGoals = departmentGoalService.getAllInstances();
            this.teamGoals = teamGoalService.getAllInstances();
            this.activityStatuses = Arrays.asList(ActivityStatus.values());
            this.users = userService.getUsers();
        } catch (Exception e) {
            // Initialize with empty lists if services fail
            this.organizationGoals = Arrays.asList();
            this.departmentGoals = Arrays.asList();
            this.teamGoals = Arrays.asList();
            this.activityStatuses = Arrays.asList(ActivityStatus.values());
            this.users = Arrays.asList();
        }
    }

    private void clearSelections() {
        selectedOrganizationGoal = null;
        selectedDepartmentGoal = null;
        selectedTeamGoal = null;
        selectedUser = null;
        selectedStatus = null;
    }

    public void onOrganizationGoalChange() {
        // Clear other goal selections when organization goal is selected
        selectedDepartmentGoal = null;
        selectedTeamGoal = null;
    }

    public void onDepartmentGoalChange() {
        // Clear other goal selections when department goal is selected
        selectedOrganizationGoal = null;
        selectedTeamGoal = null;
    }

    public void onTeamGoalChange() {
        // Clear other goal selections when team goal is selected
        selectedOrganizationGoal = null;
        selectedDepartmentGoal = null;
    }
}
