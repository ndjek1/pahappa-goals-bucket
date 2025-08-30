package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum RatingCategory {
    SUPERVISOR("Supervisor"),
    PEER("Peer");

    private final String displayName;

    RatingCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
