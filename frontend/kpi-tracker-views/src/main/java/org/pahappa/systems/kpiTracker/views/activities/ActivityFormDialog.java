package org.pahappa.systems.kpiTracker.views.activities;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.ActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.activities.Activity;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ActivityStatus;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
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
import com.googlecode.genericdao.search.Search;
import org.sers.webutils.model.RecordStatus;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private DepartmentService departmentService;
    private TeamService teamService;
    
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
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        
        loadData();
        resetModal();
    }

    private void loadData() {
        User currentUser = SharedAppData.getLoggedInUser();
        
        System.out.println("=== Loading goals for user: " + (currentUser != null ? currentUser.getFullName() : "null") + " ===");
        
        try {
            // Load organization goals - all users can see these
            this.organizationGoals = organizationGoalService.getAllInstances();
            System.out.println("Loaded " + (this.organizationGoals != null ? this.organizationGoals.size() : 0) + " organization goals");
        } catch (Exception e) {
            System.err.println("Error loading organization goals: " + e.getMessage());
            this.organizationGoals = Arrays.asList();
        }
        
        try {
            // Load department goals based on user role
            String userRole = getUserPrimaryRole(currentUser);
            if ("DEPT_LEAD".equals(userRole)) {
                System.out.println("User is DEPT_LEAD, loading department goals for user's department");
                this.departmentGoals = loadDepartmentGoalsForUser(currentUser);
            } else if ("TEAM_LEAD".equals(userRole)) {
                System.out.println("User is TEAM_LEAD, loading department goals for user's team's department");
                this.departmentGoals = loadDepartmentGoalsForTeamLead(currentUser);
            } else {
                System.out.println("User is regular user or has no special role, loading all department goals");
                this.departmentGoals = departmentGoalService.getAllInstances();
            }
            System.out.println("Loaded " + (this.departmentGoals != null ? this.departmentGoals.size() : 0) + " department goals");
        } catch (Exception e) {
            System.err.println("Error loading department goals: " + e.getMessage());
            this.departmentGoals = Arrays.asList();
        }
        
        try {
            // Load team goals based on user role
            String userRole = getUserPrimaryRole(currentUser);
            if ("TEAM_LEAD".equals(userRole)) {
                System.out.println("User is TEAM_LEAD, loading team goals for user's team");
                this.teamGoals = loadTeamGoalsForUser(currentUser);
            } else if ("DEPT_LEAD".equals(userRole)) {
                System.out.println("User is DEPT_LEAD, loading team goals for user's department");
                this.teamGoals = loadTeamGoalsForDepartmentLead(currentUser);
            } else {
                System.out.println("User is regular user or has no special role, loading all team goals");
                this.teamGoals = teamGoalService.getAllInstances();
            }
            System.out.println("Loaded " + (this.teamGoals != null ? this.teamGoals.size() : 0) + " team goals");
        } catch (Exception e) {
            System.err.println("Error loading team goals: " + e.getMessage());
            this.teamGoals = Arrays.asList();
        }
        
        try {
            this.activityStatuses = Arrays.asList(ActivityStatus.values());
            System.out.println("Loaded " + (this.activityStatuses != null ? this.activityStatuses.size() : 0) + " activity statuses");
        } catch (Exception e) {
            System.err.println("Error loading activity statuses: " + e.getMessage());
            this.activityStatuses = Arrays.asList();
        }
        
        try {
            this.users = userService.getUsers();
            System.out.println("Loaded " + (this.users != null ? this.users.size() : 0) + " users");
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            this.users = Arrays.asList();
        }
        
        System.out.println("=== Goal loading completed ===");
    }

    /**
     * Load department goals for a department lead user
     */
    private List<DepartmentGoal> loadDepartmentGoalsForUser(User user) {
        try {
            // Find the department where the user is the department head
            List<Department> userDepartments = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null && d.getDepartmentHead().equals(user))
                    .collect(Collectors.toList());
            
            if (userDepartments.isEmpty()) {
                System.out.println("No departments found for user: " + user.getFullName());
                return Arrays.asList();
            }
            
            System.out.println("Found " + userDepartments.size() + " departments for user: " + user.getFullName());
            
            // Load department goals for the user's department
            Search search = new Search();
            search.addFilterIn("department", userDepartments);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            List<DepartmentGoal> goals = departmentGoalService.getInstances(search, 0, 1000);
            System.out.println("Loaded " + goals.size() + " department goals for user: " + user.getFullName());
            return goals;
        } catch (Exception e) {
            System.err.println("Error loading department goals for user " + user.getFullName() + ": " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    /**
     * Load team goals for a team lead user
     */
    private List<TeamGoal> loadTeamGoalsForUser(User user) {
        try {
            // Find the team where the user is the team head
            List<Team> userTeams = teamService.getAllInstances()
                    .stream()
                    .filter(t -> t.getTeamHead() != null && t.getTeamHead().equals(user))
                    .collect(Collectors.toList());
            
            if (userTeams.isEmpty()) {
                System.out.println("No teams found for user: " + user.getFullName());
                return Arrays.asList();
            }
            
            System.out.println("Found " + userTeams.size() + " teams for user: " + user.getFullName());
            
            // Load team goals for the user's team
            Search search = new Search();
            search.addFilterIn("team", userTeams);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            List<TeamGoal> goals = teamGoalService.getInstances(search, 0, 1000);
            System.out.println("Loaded " + goals.size() + " team goals for user: " + user.getFullName());
            return goals;
        } catch (Exception e) {
            System.err.println("Error loading team goals for user " + user.getFullName() + ": " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    /**
     * Load department goals for a team lead user (from their team's department)
     */
    private List<DepartmentGoal> loadDepartmentGoalsForTeamLead(User user) {
        try {
            // Find the team where the user is the team head
            List<Team> userTeams = teamService.getAllInstances()
                    .stream()
                    .filter(t -> t.getTeamHead() != null && t.getTeamHead().equals(user))
                    .collect(Collectors.toList());
            
            if (userTeams.isEmpty()) {
                System.out.println("No teams found for team lead user: " + user.getFullName());
                return Arrays.asList();
            }
            
            System.out.println("Found " + userTeams.size() + " teams for team lead user: " + user.getFullName());
            
            // Get the departments from the user's teams
            List<Department> userDepartments = userTeams.stream()
                    .map(Team::getDepartment)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            
            if (userDepartments.isEmpty()) {
                System.out.println("No departments found for team lead user's teams: " + user.getFullName());
                return Arrays.asList();
            }
            
            System.out.println("Found " + userDepartments.size() + " departments for team lead user: " + user.getFullName());
            
            // Load department goals for the user's team's department
            Search search = new Search();
            search.addFilterIn("department", userDepartments);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            List<DepartmentGoal> goals = departmentGoalService.getInstances(search, 0, 1000);
            System.out.println("Loaded " + goals.size() + " department goals for team lead user: " + user.getFullName());
            return goals;
        } catch (Exception e) {
            System.err.println("Error loading department goals for team lead user " + user.getFullName() + ": " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    /**
     * Load team goals for a department lead user (from their department)
     */
    private List<TeamGoal> loadTeamGoalsForDepartmentLead(User user) {
        try {
            // Find the department where the user is the department head
            List<Department> userDepartments = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null && d.getDepartmentHead().equals(user))
                    .collect(Collectors.toList());
            
            if (userDepartments.isEmpty()) {
                System.out.println("No departments found for department lead user: " + user.getFullName());
                return Arrays.asList();
            }
            
            System.out.println("Found " + userDepartments.size() + " departments for department lead user: " + user.getFullName());
            
            // Load team goals for the user's department
            Search search = new Search();
            search.addFilterIn("department", userDepartments);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
            List<TeamGoal> goals = teamGoalService.getInstances(search, 0, 1000);
            System.out.println("Loaded " + goals.size() + " team goals for department lead user: " + user.getFullName());
            return goals;
        } catch (Exception e) {
            System.err.println("Error loading team goals for department lead user " + user.getFullName() + ": " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList();
        }
    }

    /**
     * Refresh goal lists based on current user context
     * This can be called when user context changes or when goals need to be reloaded
     */
    public void refreshGoals() {
        loadData();
    }

    /**
     * Get a summary of what goals are available to the current user
     * Useful for debugging and user feedback
     */
    public String getGoalAccessSummary() {
        User currentUser = SharedAppData.getLoggedInUser();
        if (currentUser == null) {
            return "No user logged in";
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("User: ").append(currentUser.getFullName()).append(" (");
        
        if (currentUser.hasRole("DEPT_LEAD")) {
            summary.append("Department Lead");
        } else if (currentUser.hasRole("TEAM_LEAD")) {
            summary.append("Team Lead");
        } else {
            summary.append("Regular User");
        }
        summary.append(")");
        
        summary.append(" | Organization Goals: ").append(organizationGoals != null ? organizationGoals.size() : 0);
        summary.append(" | Department Goals: ").append(departmentGoals != null ? departmentGoals.size() : 0);
        summary.append(" | Team Goals: ").append(teamGoals != null ? teamGoals.size() : 0);
        
        return summary.toString();
    }

    /**
     * Check if the current user has any goals available
     */
    public boolean hasGoalsAvailable() {
        int totalGoals = 0;
        if (organizationGoals != null) totalGoals += organizationGoals.size();
        if (departmentGoals != null) totalGoals += departmentGoals.size();
        if (teamGoals != null) totalGoals += teamGoals.size();
        return totalGoals > 0;
    }

    /**
     * Get a user-friendly message about goal access
     */
    public String getGoalAccessMessage() {
        User currentUser = SharedAppData.getLoggedInUser();
        if (currentUser == null) {
            return "Please log in to access goals";
        }
        
        if (!hasGoalsAvailable()) {
            return "No goals are currently available for your role";
        }
        
        StringBuilder message = new StringBuilder();
        message.append("Available goals for ");
        
        String userRole = getUserPrimaryRole(currentUser);
        switch (userRole) {
            case "DEPT_LEAD":
                message.append("Department Lead");
                break;
            case "TEAM_LEAD":
                message.append("Team Lead");
                break;
            case "REGULAR_USER":
                message.append("Regular User");
                break;
            default:
                message.append("User");
                break;
        }
        
        message.append(": ");
        
        if (organizationGoals != null && !organizationGoals.isEmpty()) {
            message.append(organizationGoals.size()).append(" organization goals");
        }
        
        if (departmentGoals != null && !departmentGoals.isEmpty()) {
            if (message.length() > 0) message.append(", ");
            message.append(departmentGoals.size()).append(" department goals");
        }
        
        if (teamGoals != null && !teamGoals.isEmpty()) {
            if (message.length() > 0) message.append(", ");
            message.append(teamGoals.size()).append(" team goals");
        }
        
        return message.toString();
    }

    /**
     * Get the user's primary role for goal access
     * Priority: DEPT_LEAD > TEAM_LEAD > Regular User
     */
    private String getUserPrimaryRole(User user) {
        if (user == null) return "NONE";
        
        if (user.hasRole("DEPT_LEAD")) {
            return "DEPT_LEAD";
        } else if (user.hasRole("TEAM_LEAD")) {
            return "TEAM_LEAD";
        } else {
            return "REGULAR_USER";
        }
    }

    /**
     * Check if user has access to a specific goal type
     */
    public boolean hasAccessToGoalType(String goalType) {
        User currentUser = SharedAppData.getLoggedInUser();
        if (currentUser == null) return false;
        
        String role = getUserPrimaryRole(currentUser);
        
        switch (goalType.toLowerCase()) {
            case "organization":
                return true; // All users can see organization goals
            case "department":
                return role.equals("DEPT_LEAD") || role.equals("TEAM_LEAD") || role.equals("REGULAR_USER");
            case "team":
                return role.equals("TEAM_LEAD") || role.equals("DEPT_LEAD") || role.equals("REGULAR_USER");
            default:
                return false;
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
            UiUtils.showMessageBox("Success", "Activity saved successfully!");
            
            // Handle successful save and trigger refresh
            handleSuccessfulSave();
            
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

    /**
     * Handle successful save and trigger refresh in parent views
     */
    private void handleSuccessfulSave() {
        try {
            // Hide the dialog
            hide();
            
            // Trigger dialogReturn event for automatic refresh
            PrimeFaces.current().executeScript("PF('activityFormDialog').fireEvent('dialogReturn');");
            
            // Reset the modal for next use
            resetModal();
            
            System.out.println("Activity saved successfully, dialog closed and refresh triggered");
        } catch (Exception e) {
            System.err.println("Error handling successful save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void show() {
        // Show the dialog using the DialogForm base class method
        super.show(null);
    }
    
    public void hide() {
        // Hide the dialog using the DialogForm base class method
        super.hide();
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
}
