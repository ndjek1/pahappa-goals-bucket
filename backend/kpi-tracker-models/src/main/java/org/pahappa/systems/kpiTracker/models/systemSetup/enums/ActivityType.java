package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum ActivityType {
    QUALITATIVE("Qualitative    "),
    QUANTITATIVE("Quantitative");

    private final String displayName;

    ActivityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
