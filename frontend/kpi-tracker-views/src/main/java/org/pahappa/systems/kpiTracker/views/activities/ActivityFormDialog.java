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
import org.primefaces.PrimeFaces;
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
    
    // Add edit field like user dialogs
    private boolean edit;

    public ActivityFormDialog() {
        super(HyperLinks.ACTIVITY_FORM_DIALOG, 1200, 750);
    }

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        
        loadData();
        resetModal();
    }

    private void loadData() {
        try {
            this.organizationGoals = organizationGoalService.getAllInstances();
        } catch (Exception e) {
            this.organizationGoals = Arrays.asList();
        }
        
        try {
            this.departmentGoals = departmentGoalService.getAllInstances();
        } catch (Exception e) {
            this.departmentGoals = Arrays.asList();
        }
        
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
    }

    @Override
    public void persist() throws ValidationFailedException, OperationFailedException {
        try {
            if (model.getTitle() == null || model.getTitle().trim().isEmpty()) {
                UiUtils.showMessageBox("Missing title", "Activity must have a title.");
                return;
            }
            
            if (selectedUser == null) {
                UiUtils.showMessageBox("Missing user", "Activity must be assigned to a user.");
                return;
            }
            
            // Set the selected user
            model.setUser(selectedUser);
            
            // Set the selected goal (only one can be selected)
            if (selectedOrganizationGoal != null) {
                model.setOrganizationGoal(selectedOrganizationGoal);
            } else if (selectedDepartmentGoal != null) {
                model.setDepartmentGoal(selectedDepartmentGoal);
            } else if (selectedTeamGoal != null) {
                model.setTeamGoal(selectedTeamGoal);
            }
            
            // Set status if selected
            if (selectedStatus != null) {
                model.setStatus(selectedStatus);
            }
            
            activityService.saveInstance(model);
        } catch (ValidationFailedException e) {
            UiUtils.ComposeFailure("Validation Error", e.getMessage());
            throw e;
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Operation Error", e.getMessage());
            throw e;
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to save activity: " + e.getMessage());
            throw new OperationFailedException("Failed to save activity: " + e.getMessage(), e);
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
        if (this.model != null && this.model.getId() != null) {
            this.edit = true;
            // Set selections based on existing model
            if (model.getOrganizationGoal() != null) {
                selectedOrganizationGoal = model.getOrganizationGoal();
            } else if (model.getDepartmentGoal() != null) {
                selectedDepartmentGoal = model.getDepartmentGoal();
            } else if (model.getTeamGoal() != null) {
                selectedTeamGoal = model.getTeamGoal();
            }
            selectedUser = model.getUser();
            selectedStatus = model.getStatus();
        } else {
            this.edit = false;
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
    
    public void show() {
        // Show the dialog using the DialogForm base class method
        super.show(null);
    }
    
    public void hide() {
        // Hide the dialog using the DialogForm base class method
        super.hide();
    }
}
