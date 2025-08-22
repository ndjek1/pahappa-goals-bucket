package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "organizationalActivityFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class OrganizationalActivityFormDialog extends DialogForm<Activity> {

    private static final long serialVersionUID = 1L;
    
    private ActivityService activityService;
    private OrganizationGoalService organizationGoalService;
    private UserService userService;
    
    private List<OrganizationGoal> organizationGoals;
    private List<ActivityStatus> activityStatuses;
    private List<User> users;
    private List<String> activityTypes;
    private List<String> priorities;
    
    private OrganizationGoal selectedOrganizationGoal;
    private User selectedUser;
    private ActivityStatus selectedStatus;
    private String selectedActivityType;
    private String selectedPriority;
    
    // Add edit field like user dialogs
    private boolean edit;

    public OrganizationalActivityFormDialog() {
        super(HyperLinks.ORGANIZATIONAL_ACTIVITY_FORM_DIALOG, 1200, 750);
    }

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
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
        this.activityTypes = Arrays.asList("Strategic", "Tactical", "Operational", "Project", "Maintenance", "Research");
        this.priorities = Arrays.asList("Low", "Medium", "High", "Critical");
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
            
            if (selectedOrganizationGoal == null) {
                UiUtils.showMessageBox("Missing organization goal", "Organizational activity must be linked to an organization goal.");
                return;
            }
            
            // Set the selected user
            model.setUser(selectedUser);
            
            // Set the organization goal (clear other goal types)
            model.setOrganizationGoal(selectedOrganizationGoal);
            model.setDepartmentGoal(null);
            model.setTeamGoal(null);
            
            // Set status if selected
            if (selectedStatus != null) {
                model.setStatus(selectedStatus);
            }
            
            // Set activity type and priority if selected
            if (selectedActivityType != null) {
                // You might want to add an activityType field to the Activity model
                // For now, we'll store it in a custom field or extend the model
            }
            
            activityService.saveInstance(model);
        } catch (ValidationFailedException e) {
            UiUtils.ComposeFailure("Validation Error", e.getMessage());
            throw e;
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Operation Error", e.getMessage());
            throw e;
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to save organizational activity: " + e.getMessage());
            throw new OperationFailedException("Failed to save organizational activity: " + e.getMessage(), e);
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
            }
            selectedUser = model.getUser();
            selectedStatus = model.getStatus();
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        selectedOrganizationGoal = null;
        selectedUser = null;
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
