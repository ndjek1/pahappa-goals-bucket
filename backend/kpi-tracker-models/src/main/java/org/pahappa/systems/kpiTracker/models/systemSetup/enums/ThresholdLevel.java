package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum ThresholdLevel {
    // Enum constants with their corresponding display names
    INDIVIDUAL("Individual"),
    TEAM("Team"),
    DEPARTMENT("Department"),
    ORGANISATION("Organisation"); // Added Organisation as it was in the previous screenshot

    // Field to store the display name
    private final String displayName;
    
    ThresholdLevel(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}
