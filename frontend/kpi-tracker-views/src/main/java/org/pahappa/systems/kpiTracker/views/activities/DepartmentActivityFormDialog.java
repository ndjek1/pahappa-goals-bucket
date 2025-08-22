package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
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

@ManagedBean(name = "departmentActivityFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class DepartmentActivityFormDialog extends DialogForm<Activity> {

    private static final long serialVersionUID = 1L;
    
    private ActivityService activityService;
    private DepartmentGoalService departmentGoalService;
    private UserService userService;
    
    private List<DepartmentGoal> departmentGoals;
    private List<ActivityStatus> activityStatuses;
    private List<User> users;
    private List<String> activityTypes;
    private List<String> priorities;
    
    private DepartmentGoal selectedDepartmentGoal;
    private User selectedUser;
    private ActivityStatus selectedStatus;
    private String selectedActivityType;
    private String selectedPriority;
    
    // Add edit field like user dialogs
    private boolean edit;

    public DepartmentActivityFormDialog() {
        super(HyperLinks.DEPARTMENT_ACTIVITY_FORM_DIALOG, 1200, 750);
    }

    @PostConstruct
    public void init() {
        this.activityService = ApplicationContextProvider.getBean(ActivityService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        
        loadData();
        resetModal();
    }

    private void loadData() {
        try {
            this.departmentGoals = departmentGoalService.getAllInstances();
        } catch (Exception e) {
            this.departmentGoals = Arrays.asList();
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
            
            if (selectedDepartmentGoal == null) {
                UiUtils.showMessageBox("Missing department goal", "Department activity must be linked to a department goal.");
                return;
            }
            
            // Set the selected user
            model.setUser(selectedUser);
            
            // Set the department goal (clear other goal types)
            model.setDepartmentGoal(selectedDepartmentGoal);
            model.setOrganizationGoal(null);
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
            UiUtils.ComposeFailure("Error", "Failed to save department activity: " + e.getMessage());
            throw new OperationFailedException("Failed to save department activity: " + e.getMessage(), e);
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
            if (model.getDepartmentGoal() != null) {
                selectedDepartmentGoal = model.getDepartmentGoal();
            }
            selectedUser = model.getUser();
            selectedStatus = model.getStatus();
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        selectedDepartmentGoal = null;
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
