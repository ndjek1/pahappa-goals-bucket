package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.DepartmentActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
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

@ManagedBean(name = "departmentActivityFormDialog", eager = true)
@Getter
@Setter
@SessionScoped
public class DepartmentActivityFormDialog extends DialogForm<DepartmentActivity> {

    private static final long serialVersionUID = 1L;
    
    private DepartmentActivityService departmentActivityService;
    private DepartmentGoalService departmentGoalService;
    private UserService userService;
    private DepartmentService departmentService;
    
    private List<DepartmentGoal> departmentGoals;
    private List<ActivityStatus> activityStatuses;
    private List<User> users;
    private List<ActivityType> activityTypes;
    private List<ActivityPriority> priorities;
    
    private DepartmentGoal selectedDepartmentGoal;
    private ActivityStatus selectedStatus;
    private ActivityType selectedActivityType;
    private ActivityPriority selectedPriority;
    private Department department;
    private User loggedinUser;


    // Add edit field like user dialogs
    private boolean edit;

    public DepartmentActivityFormDialog() {
        super(HyperLinks.DEPARTMENT_ACTIVITY_FORM_DIALOG, 700, 400);
    }

    @PostConstruct
    public void init() {
        this.departmentActivityService = ApplicationContextProvider.getBean(DepartmentActivityService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.loggedinUser = SharedAppData.getLoggedInUser();
        loadData();
        resetModal();
        loadDepartment();
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
        this.activityTypes = Arrays.asList(ActivityType.values());
        this.priorities = Arrays.asList(ActivityPriority.values());

    }

    public void loadDepartment() {
        if (this.loggedinUser != null && this.loggedinUser.hasRole("Department Lead")) {
            this.department = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null && d.getDepartmentHead().equals(this.loggedinUser))
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

            if (selectedDepartmentGoal != null) model.setDepartmentGoal(selectedDepartmentGoal);
            if (selectedStatus != null)        model.setStatus(selectedStatus);
            if (department != null)            model.setDepartment(department);
            if (selectedActivityType != null)  model.setActivityType(selectedActivityType);
            if (selectedPriority != null)      model.setPriority(selectedPriority);

            departmentActivityService.saveInstance(model);
            resetModal();
            hide();
        } catch (ValidationFailedException | OperationFailedException e) {
            UiUtils.ComposeFailure("Error", e.getMessage());
            throw e;
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Failed to save department activity: " + e.getMessage());
            throw new OperationFailedException("Failed to save department activity: " + e.getMessage(), e);
        }
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new DepartmentActivity();
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
            selectedStatus = model.getStatus();
            selectedActivityType = model.getActivityType();
            selectedPriority = model.getPriority();
        } else {
            this.edit = false;
        }
    }

    private void clearSelections() {
        selectedDepartmentGoal = null;
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
