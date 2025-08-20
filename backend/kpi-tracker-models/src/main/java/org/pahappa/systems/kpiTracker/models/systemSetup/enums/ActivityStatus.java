package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum ActivityStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    BLOCKED("Blocked");

    private final String displayName;

    ActivityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}