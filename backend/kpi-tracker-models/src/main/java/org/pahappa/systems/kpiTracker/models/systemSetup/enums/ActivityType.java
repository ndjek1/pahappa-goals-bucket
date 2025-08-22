package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum ActivityType {
    STRATEGIC("Strategic"),
    TACTICAL("Tactical"),
    OPERATIONAL("Operational"),
    PROJECT("Project"),
    MAINTENANCE("Maintenance"),
    RESEARCH("Research");

    private final String displayName;

    ActivityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
