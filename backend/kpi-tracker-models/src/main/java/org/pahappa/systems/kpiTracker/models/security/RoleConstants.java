package org.pahappa.systems.kpiTracker.models.security;

public final class RoleConstants {
    private RoleConstants() {}

    @SystemRole(name = "Chief Executive Officer", description = "Top-level role with access to all modules and reports")
    public static final String ROLE_CEO = "CEO";

    @SystemRole(name = "Human Resources", description = "Manages users, roles, review cycles, and global configurations")
    public static final String ROLE_HR = "HR";

    @SystemRole(name = "Department Lead", description = "Manages department-level goals, KPIs, and activities")
    public static final String ROLE_DEPARTMENT_LEAD = "Department Lead";

    @SystemRole(name = "Team Lead", description = "Manages team-level goals, KPIs, and activities")
    public static final String ROLE_TEAM_LEAD = "Team Lead";

    @SystemRole(name = "Individual", description = "Base role for employees who manage their own goals, KPIs, and activities")
    public static final String ROLE_INDIVIDUAL = "Individual";
}
