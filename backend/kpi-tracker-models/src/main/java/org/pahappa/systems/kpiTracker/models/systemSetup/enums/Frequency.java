package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum Frequency {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    BIANNUALLY("Biannually"),
    ANNUALLY("Annually");

    private final String displayName;

    Frequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}