package org.pahappa.systems.kpiTracker.views.kpis;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Backing bean for Department KPI Details view
 * Manages detailed display of department KPIs with related activities and progress tracking
 */
@Component
@ManagedBean
@ViewScoped
public class DepartmentKPIDetails implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Autowired
    private KpisService kpisService;

    
    private KPI selectedKPI;
    private String kpiId;
    
    // Progress tracking
    private Double currentProgressValue;
    private Date progressUpdateDate;
    private List<ProgressData> progressHistory;
    private boolean showProgressDialog;
    
    // Chart data for monthly performance trend
    private List<MonthlyProgress> monthlyProgressData;
    
    @PostConstruct
    public void init() {
        // Get KPI ID from request parameters
        FacesContext context = FacesContext.getCurrentInstance();
        String kpiIdParam = context.getExternalContext().getRequestParameterMap().get("kpiId");
        
        if (kpiIdParam != null && !kpiIdParam.trim().isEmpty()) {
            try {
                Long id = Long.parseLong(kpiIdParam);
                loadKPI(id);
                loadRelatedActivities();
                initializeProgressData();
                generateMonthlyProgressData();
            } catch (NumberFormatException e) {
                UiUtils.ComposeFailure("Error", "Invalid KPI ID");
            }
        }
    }
    
    /**
     * Load KPI by ID
     */
    public void loadKPI(Long id) {
        try {
            selectedKPI = kpisService.getInstanceByID(id.toString());
            if (selectedKPI == null) {
                UiUtils.ComposeFailure("Error", "KPI not found");
            }
        } catch (Exception e) {
            UiUtils.ComposeFailure("Error", "Error loading KPI: " + e.getMessage());
        }
    }
    
    /**
     * Load activities related to the selected KPI
     */
    public void loadRelatedActivities() {
        if (selectedKPI != null && selectedKPI.getId() != null) {
            try {
                // Load activities that are linked to this KPI
                // This would depend on your data model - you might need to adjust the query
                // For now, we'll set it to null until the relationship is properly defined
            } catch (Exception e) {
                UiUtils.ComposeFailure("Error", "Error loading related activities: " + e.getMessage());
            }
        }
    }
    
    /**
     * Initialize progress tracking data
     */
    public void initializeProgressData() {
        if (selectedKPI != null) {
            // Set current progress value from KPI
            currentProgressValue = selectedKPI.getCurrentValue() != null ? selectedKPI.getCurrentValue() : 0.0;
            progressUpdateDate = new Date();
            
            // Initialize progress history (this would come from database in real implementation)
            progressHistory = new ArrayList<>();
            if (selectedKPI.getCurrentValue() != null && selectedKPI.getTargetValue() != null) {
                double percentage = (selectedKPI.getCurrentValue() / selectedKPI.getTargetValue()) * 100;
                progressHistory.add(new ProgressData(selectedKPI.getCurrentValue(), percentage, new Date()));
            }
        }
    }
    
    /**
     * Generate monthly progress data for chart
     */
    public void generateMonthlyProgressData() {
        monthlyProgressData = new ArrayList<>();
        
        // Generate sample monthly data (in real implementation, this would come from database)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2025);
        
        double baseProgress = 65.0; // Starting progress in January
        
        for (int month = 0; month < 12; month++) {
            cal.set(Calendar.MONTH, month);
            Date monthDate = cal.getTime();
            
            // Simulate realistic progress fluctuations
            double monthProgress = baseProgress + (month * 2.5) + (Math.random() * 10 - 5);
            monthProgress = Math.max(60.0, Math.min(95.0, monthProgress)); // Keep within bounds
            
            monthlyProgressData.add(new MonthlyProgress(monthDate, monthProgress));
        }
    }
    
    /**
     * Show progress update dialog
     */
    public void showProgressDialog() {
        showProgressDialog = true;
        // Pre-populate with current value
        if (selectedKPI != null) {
            currentProgressValue = selectedKPI.getCurrentValue() != null ? selectedKPI.getCurrentValue() : 0.0;
        }
        progressUpdateDate = new Date();
    }
    
    /**
     * Hide progress update dialog
     */
    public void hideProgressDialog() {
        showProgressDialog = false;
        currentProgressValue = null;
    }
    
    /**
     * Update KPI progress
     */
    public void updateProgress() {
        if (selectedKPI != null && currentProgressValue != null) {
            try {
                // Update KPI current value
                selectedKPI.setCurrentValue(currentProgressValue);
                
                // Calculate new accomplishment percentage
                if (selectedKPI.getTargetValue() != null && selectedKPI.getTargetValue() > 0) {
                    double percentage = (currentProgressValue / selectedKPI.getTargetValue()) * 100;
                    selectedKPI.setAccomplishmentPercentage(percentage);
                }
                
                // Save updated KPI
                kpisService.saveInstance(selectedKPI);
                
                // Add to progress history
                if (progressHistory == null) {
                    progressHistory = new ArrayList<>();
                }
                
                double percentage = selectedKPI.getAccomplishmentPercentage() != null ? 
                    selectedKPI.getAccomplishmentPercentage() : 0.0;
                progressHistory.add(new ProgressData(currentProgressValue, percentage, new Date()));
                
                // Regenerate chart data
                generateMonthlyProgressData();
                
                UiUtils.showMessageBox("Progress Updated", "KPI progress has been updated successfully");
                hideProgressDialog();
                
            } catch (Exception e) {
                UiUtils.ComposeFailure("Error", "Failed to update progress: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get current accomplishment percentage
     */
    public double getCurrentAccomplishmentPercentage() {
        if (selectedKPI != null && selectedKPI.getAccomplishmentPercentage() != null) {
            return selectedKPI.getAccomplishmentPercentage();
        }
        return 0.0;
    }
    
    /**
     * Navigate back to department KPIs list
     */
    public String backToKPIs() {
        return HyperLinks.DEPARTMENT_KPIS_VIEW;
    }
    
    /**
     * Delete the selected KPI
     */
    public void deleteKPI() {
        if (selectedKPI != null && selectedKPI.getId() != null) {
            try {
                kpisService.deleteInstance(selectedKPI);
                UiUtils.showMessageBox("KPI deleted successfully", "Success");
                // Redirect back to KPIs list
                FacesContext.getCurrentInstance().getExternalContext().redirect(
                    FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + 
                    HyperLinks.DEPARTMENT_KPIS_VIEW
                );
            } catch (Exception e) {
                UiUtils.ComposeFailure("Error", "Error deleting KPI: " + e.getMessage());
            }
        }
    }
    
    /**
     * Prepare KPI for viewing (called from list view)
     */
    public String prepareForViewing(Long kpiId) {
        this.kpiId = kpiId.toString();
        return HyperLinks.DEPARTMENT_KPI_DETAILS_VIEW + "?faces-redirect=true&kpiId=" + kpiId;
    }
    
    // Getters and Setters
    public KPI getSelectedKPI() {
        return selectedKPI;
    }
    
    public void setSelectedKPI(KPI selectedKPI) {
        this.selectedKPI = selectedKPI;
    }

    
    public String getKpiId() {
        return kpiId;
    }
    
    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }
    
    public Double getCurrentProgressValue() {
        return currentProgressValue;
    }
    
    public void setCurrentProgressValue(Double currentProgressValue) {
        this.currentProgressValue = currentProgressValue;
    }
    
    public Date getProgressUpdateDate() {
        return progressUpdateDate;
    }
    
    public void setProgressUpdateDate(Date progressUpdateDate) {
        this.progressUpdateDate = progressUpdateDate;
    }
    
    public List<ProgressData> getProgressHistory() {
        return progressHistory;
    }
    
    public void setProgressHistory(List<ProgressData> progressHistory) {
        this.progressHistory = progressHistory;
    }
    
    public boolean isShowProgressDialog() {
        return showProgressDialog;
    }
    
    public void setShowProgressDialog(boolean showProgressDialog) {
        this.showProgressDialog = showProgressDialog;
    }
    
    public List<MonthlyProgress> getMonthlyProgressData() {
        return monthlyProgressData;
    }
    
    public void setMonthlyProgressData(List<MonthlyProgress> monthlyProgressData) {
        this.monthlyProgressData = monthlyProgressData;
    }
    
    // Inner classes for data management
    
    /**
     * Progress data for tracking individual updates
     */
    public static class ProgressData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Double value;
        private Double percentage;
        private Date updateDate;
        
        public ProgressData(Double value, Double percentage, Date updateDate) {
            this.value = value;
            this.percentage = percentage;
            this.updateDate = updateDate;
        }
        
        // Getters and Setters
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
        
        public Date getUpdateDate() { return updateDate; }
        public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }
    }
    
    /**
     * Monthly progress data for chart visualization
     */
    public static class MonthlyProgress implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Date month;
        private Double progress;
        
        public MonthlyProgress(Date month, Double progress) {
            this.month = month;
            this.progress = progress;
        }
        
        // Getters and Setters
        public Date getMonth() { return month; }
        public void setMonth(Date month) { this.month = month; }
        
        public Double getProgress() { return progress; }
        public void setProgress(Double progress) { this.progress = progress; }
        
        // Helper method for chart labels
        public String getMonthLabel() {
            if (month != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(month);
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                                 "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                return months[cal.get(Calendar.MONTH)];
            }
            return "";
        }
    }
}
