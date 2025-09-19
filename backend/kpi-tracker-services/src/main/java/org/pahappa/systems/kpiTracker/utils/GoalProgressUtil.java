package org.pahappa.systems.kpiTracker.utils;

public class GoalProgressUtil {
    public String getProgressColor(double progress) {
        // Assuming getProgress() returns the percentage value

        if (progress < 40) {
            return "#ef4444"; // Red for low progress
        } else if (progress < 75) {
            return "#f59e0b"; // Amber/Yellow for medium progress
        } else {
            return "#22c55e"; // Green for high progress
        }
    }
}
