package org.pahappa.systems.kpiTracker.models.security;

public final class PermissionConstants {
    private PermissionConstants() {
    }

    @SystemPermission(name = "Api user", description = "Has role for api users")
    public static final String PERM_API_USER = "Api User";

    @SystemPermission(name = "View Goals", description = "Can view goals at any level")
    public static final String PERM_VIEW_GOALS = "View Goals";

    @SystemPermission(name = "Create Goals", description = "Can create goals")
    public static final String PERM_CREATE_GOALS = "Create Goals";

    @SystemPermission(name = "Edit Goals", description = "Can edit existing goals")
    public static final String PERM_EDIT_GOALS = "Edit Goals";

    @SystemPermission(name = "Delete Goals", description = "Can delete goals")
    public static final String PERM_DELETE_GOALS = "Delete Goals";

    @SystemPermission(name = "Manage KPIs", description = "Can create, edit, and delete KPIs")
    public static final String PERM_MANAGE_KPIS = "Manage KPIs";

    @SystemPermission(name = "View Reports", description = "Can view system reports")
    public static final String PERM_VIEW_REPORTS = "View Reports";

    @SystemPermission(name = "Manage Review Cycles", description = "Can create and configure review cycles")
    public static final String PERM_MANAGE_REVIEW_CYCLES = "Manage Review Cycles";

    @SystemPermission(name = "Manage Users", description = "Can create, edit, and delete users")
    public static final String PERM_MANAGE_USERS = "Manage Users";

    @SystemPermission(name = "Manage Roles", description = "Can create, edit, and delete roles")
    public static final String PERM_MANAGE_ROLES = "Manage Roles";

    @SystemPermission(name = "Manage Permissions", description = "Can create, edit, and delete permissions")
    public static final String PERM_MANAGE_PERMISSIONS = "Manage Permissions";

    @SystemPermission(name = "Manage Departments", description = "Can create, edit, and delete departments")
    public static final String PERM_MANAGE_DEPARTMENTS = "Manage Departments";

    @SystemPermission(name = "Manage Teams", description = "Can create, edit, and delete teams")
    public static final String PERM_MANAGE_TEAMS = "Manage Teams";

}
