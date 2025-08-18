package org.pahappa.systems.kpiTracker.models.goals;

public enum GoalStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    ARCHIVED("Archived"); // Added Organisation as it was in the previous screenshot

    // Field to store the display name
    private final String displayName;

    GoalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
