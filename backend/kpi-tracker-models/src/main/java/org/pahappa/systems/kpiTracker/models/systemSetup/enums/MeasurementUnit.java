package org.pahappa.systems.kpiTracker.models.systemSetup.enums;

public enum MeasurementUnit {
    PERCENTAGE("%"),
    NUMBER("Number"),
    CURRENCY("Currency"),
    DAYS("Days"),
    HOURS("Hours"),
    ITEMS("Items"),
    SCORE("Score");

    private final String displayName;

    MeasurementUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
