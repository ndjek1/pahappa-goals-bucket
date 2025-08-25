package org.pahappa.systems.kpiTracker.models.security;

public final class PermissionConstants {
    private PermissionConstants() {}

    @SystemPermission(name = "API User", description = "Has role for API users")
    public static final String PERM_API_USER = "Api User";

    // ------------------ GOALS ------------------
    @SystemPermission(name = "View Organization Goals", description = "Can view organization goals")
    public static final String PERM_VIEW_ORG_GOALS = "View Organization Goals";
    @SystemPermission(name = "Create Organization Goals", description = "Can create organization goals")
    public static final String PERM_CREATE_ORG_GOALS = "Create Organization Goals";
    @SystemPermission(name = "Edit Organization Goals", description = "Can edit organization goals")
    public static final String PERM_EDIT_ORG_GOALS = "Edit Organization Goals";
    @SystemPermission(name = "Delete Organization Goals", description = "Can delete organization goals")
    public static final String PERM_DELETE_ORG_GOALS = "Delete Organization Goals";

    @SystemPermission(name = "View Department Goals", description = "Can view department goals")
    public static final String PERM_VIEW_DEPT_GOALS = "View Department Goals";
    @SystemPermission(name = "Create Department Goals", description = "Can create department goals")
    public static final String PERM_CREATE_DEPT_GOALS = "Create Department Goals";
    @SystemPermission(name = "Edit Department Goals", description = "Can edit department goals")
    public static final String PERM_EDIT_DEPT_GOALS = "Edit Department Goals";
    @SystemPermission(name = "Delete Department Goals", description = "Can delete department goals")
    public static final String PERM_DELETE_DEPT_GOALS = "Delete Department Goals";

    @SystemPermission(name = "View Team Goals", description = "Can view team goals")
    public static final String PERM_VIEW_TEAM_GOALS = "View Team Goals";
    @SystemPermission(name = "Create Team Goals", description = "Can create team goals")
    public static final String PERM_CREATE_TEAM_GOALS = "Create Team Goals";
    @SystemPermission(name = "Edit Team Goals", description = "Can edit team goals")
    public static final String PERM_EDIT_TEAM_GOALS = "Edit Team Goals";
    @SystemPermission(name = "Delete Team Goals", description = "Can delete team goals")
    public static final String PERM_DELETE_TEAM_GOALS = "Delete Team Goals";

    @SystemPermission(name = "View Individual Goals", description = "Can view individual goals")
    public static final String PERM_VIEW_INDIVIDUAL_GOALS = "View Individual Goals";
    @SystemPermission(name = "Create Individual Goals", description = "Can create individual goals")
    public static final String PERM_CREATE_INDIVIDUAL_GOALS = "Create Individual Goals";
    @SystemPermission(name = "Edit Individual Goals", description = "Can edit individual goals")
    public static final String PERM_EDIT_INDIVIDUAL_GOALS = "Edit Individual Goals";
    @SystemPermission(name = "Delete Individual Goals", description = "Can delete individual goals")
    public static final String PERM_DELETE_INDIVIDUAL_GOALS = "Delete Individual Goals";

    // ------------------ ACTIVITIES ------------------
    @SystemPermission(name = "View Department Activities", description = "Can view department activities")
    public static final String PERM_VIEW_DEPT_ACTIVITIES = "View Department Activities";
    @SystemPermission(name = "Create Department Activities", description = "Can create department activities")
    public static final String PERM_CREATE_DEPT_ACTIVITIES = "Create Department Activities";
    @SystemPermission(name = "Edit Department Activities", description = "Can edit department activities")
    public static final String PERM_EDIT_DEPT_ACTIVITIES = "Edit Department Activities";
    @SystemPermission(name = "Delete Department Activities", description = "Can delete department activities")
    public static final String PERM_DELETE_DEPT_ACTIVITIES = "Delete Department Activities";

    @SystemPermission(name = "View Team Activities", description = "Can view team activities")
    public static final String PERM_VIEW_TEAM_ACTIVITIES = "View Team Activities";
    @SystemPermission(name = "Create Team Activities", description = "Can create team activities")
    public static final String PERM_CREATE_TEAM_ACTIVITIES = "Create Team Activities";
    @SystemPermission(name = "Edit Team Activities", description = "Can edit team activities")
    public static final String PERM_EDIT_TEAM_ACTIVITIES = "Edit Team Activities";
    @SystemPermission(name = "Delete Team Activities", description = "Can delete team activities")
    public static final String PERM_DELETE_TEAM_ACTIVITIES = "Delete Team Activities";

    @SystemPermission(name = "View Individual Activities", description = "Can view individual activities")
    public static final String PERM_VIEW_INDIVIDUAL_ACTIVITIES = "View Individual Activities";
    @SystemPermission(name = "Create Individual Activities", description = "Can create individual activities")
    public static final String PERM_CREATE_INDIVIDUAL_ACTIVITIES = "Create Individual Activities";
    @SystemPermission(name = "Edit Individual Activities", description = "Can edit individual activities")
    public static final String PERM_EDIT_INDIVIDUAL_ACTIVITIES = "Edit Individual Activities";
    @SystemPermission(name = "Delete Individual Activities", description = "Can delete individual activities")
    public static final String PERM_DELETE_INDIVIDUAL_ACTIVITIES = "Delete Individual Activities";

    // ------------------ KPIs ------------------
    @SystemPermission(name = "View KPIs", description = "Can view KPIs")
    public static final String PERM_VIEW_KPIS = "View KPIs";
    @SystemPermission(name = "Create KPIs", description = "Can create KPIs")
    public static final String PERM_CREATE_KPIS = "Create KPIs";
    @SystemPermission(name = "Edit KPIs", description = "Can edit KPIs")
    public static final String PERM_EDIT_KPIS = "Edit KPIs";
    @SystemPermission(name = "Delete KPIs", description = "Can delete KPIs")
    public static final String PERM_DELETE_KPIS = "Delete KPIs";

    // ------------------ OTHER ENTITIES ------------------
    @SystemPermission(name = "View Departments", description = "Can view departments")
    public static final String PERM_VIEW_DEPARTMENTS = "View Departments";
    @SystemPermission(name = "Create Departments", description = "Can create departments")
    public static final String PERM_CREATE_DEPARTMENTS = "Create Departments";
    @SystemPermission(name = "Edit Departments", description = "Can edit departments")
    public static final String PERM_EDIT_DEPARTMENTS = "Edit Departments";
    @SystemPermission(name = "Delete Departments", description = "Can delete departments")
    public static final String PERM_DELETE_DEPARTMENTS = "Delete Departments";

    @SystemPermission(name = "View Teams", description = "Can view teams")
    public static final String PERM_VIEW_TEAMS = "View Teams";
    @SystemPermission(name = "Create Teams", description = "Can create teams")
    public static final String PERM_CREATE_TEAMS = "Create Teams";
    @SystemPermission(name = "Edit Teams", description = "Can edit teams")
    public static final String PERM_EDIT_TEAMS = "Edit Teams";
    @SystemPermission(name = "Delete Teams", description = "Can delete teams")
    public static final String PERM_DELETE_TEAMS = "Delete Teams";

    @SystemPermission(name = "View Users", description = "Can view users")
    public static final String PERM_VIEW_USERS = "View Users";
    @SystemPermission(name = "Create Users", description = "Can create users")
    public static final String PERM_CREATE_USERS = "Create Users";
    @SystemPermission(name = "Edit Users", description = "Can edit users")
    public static final String PERM_EDIT_USERS = "Edit Users";
    @SystemPermission(name = "Delete Users", description = "Can delete users")
    public static final String PERM_DELETE_USERS = "Delete Users";

    @SystemPermission(name = "View Global Weights", description = "Can view global weights")
    public static final String PERM_VIEW_GLOBAL_WEIGHTS = "View Global Weights";
    @SystemPermission(name = "Create Global Weights", description = "Can create global weights")
    public static final String PERM_CREATE_GLOBAL_WEIGHTS = "Create Global Weights";
    @SystemPermission(name = "Edit Global Weights", description = "Can edit global weights")
    public static final String PERM_EDIT_GLOBAL_WEIGHTS = "Edit Global Weights";
    @SystemPermission(name = "Delete Global Weights", description = "Can delete global weights")
    public static final String PERM_DELETE_GLOBAL_WEIGHTS = "Delete Global Weights";

    @SystemPermission(name = "View Review Cycles", description = "Can view review cycles")
    public static final String PERM_VIEW_REVIEW_CYCLES = "View Review Cycles";
    @SystemPermission(name = "Create Review Cycles", description = "Can create review cycles")
    public static final String PERM_CREATE_REVIEW_CYCLES = "Create Review Cycles";
    @SystemPermission(name = "Edit Review Cycles", description = "Can edit review cycles")
    public static final String PERM_EDIT_REVIEW_CYCLES = "Edit Review Cycles";
    @SystemPermission(name = "Delete Review Cycles", description = "Can delete review cycles")
    public static final String PERM_DELETE_REVIEW_CYCLES = "Delete Review Cycles";

    @SystemPermission(name = "View Thresholds", description = "Can view thresholds")
    public static final String PERM_VIEW_THRESHOLDS = "View Thresholds";
    @SystemPermission(name = "Create Thresholds", description = "Can create thresholds")
    public static final String PERM_CREATE_THRESHOLDS = "Create Thresholds";
    @SystemPermission(name = "Edit Thresholds", description = "Can edit thresholds")
    public static final String PERM_EDIT_THRESHOLDS = "Edit Thresholds";
    @SystemPermission(name = "Delete Thresholds", description = "Can delete thresholds")
    public static final String PERM_DELETE_THRESHOLDS = "Delete Thresholds";

    @SystemPermission(name = "View Organizational Fit Categories", description = "Can view org fit categories")
    public static final String PERM_VIEW_ORG_FIT_CATEGORIES = "View Organizational Fit Categories";
    @SystemPermission(name = "Create Organizational Fit Categories", description = "Can create org fit categories")
    public static final String PERM_CREATE_ORG_FIT_CATEGORIES = "Create Organizational Fit Categories";
    @SystemPermission(name = "Edit Organizational Fit Categories", description = "Can edit org fit categories")
    public static final String PERM_EDIT_ORG_FIT_CATEGORIES = "Edit Organizational Fit Categories";
    @SystemPermission(name = "Delete Organizational Fit Categories", description = "Can delete org fit categories")
    public static final String PERM_DELETE_ORG_FIT_CATEGORIES = "Delete Organizational Fit Categories";

    @SystemPermission(name = "View Organizational Fit Category Items", description = "Can view org fit category items")
    public static final String PERM_VIEW_ORG_FIT_CATEGORY_ITEMS = "View Organizational Fit Category Items";
    @SystemPermission(name = "Create Organizational Fit Category Items", description = "Can create org fit category items")
    public static final String PERM_CREATE_ORG_FIT_CATEGORY_ITEMS = "Create Organizational Fit Category Items";
    @SystemPermission(name = "Edit Organizational Fit Category Items", description = "Can edit org fit category items")
    public static final String PERM_EDIT_ORG_FIT_CATEGORY_ITEMS = "Edit Organizational Fit Category Items";
    @SystemPermission(name = "Delete Organizational Fit Category Items", description = "Can delete org fit category items")
    public static final String PERM_DELETE_ORG_FIT_CATEGORY_ITEMS = "Delete Organizational Fit Category Items";

    // ------------------ SYSTEM LEVEL ------------------
    @SystemPermission(name = "Manage Roles", description = "Can create, edit, and delete roles")
    public static final String PERM_MANAGE_ROLES = "Manage Roles";

    @SystemPermission(name = "Manage Permissions", description = "Can create, edit, and delete permissions")
    public static final String PERM_MANAGE_PERMISSIONS = "Manage Permissions";

    @SystemPermission(name = "View Reports", description = "Can view reports")
    public static final String PERM_VIEW_REPORTS = "View Reports";
}
