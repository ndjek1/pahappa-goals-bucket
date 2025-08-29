package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "kpiDetailView")
@Getter
@Setter
@ViewScoped
public class KpiDetailView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private KpisService kpisService;
    private KpiUpdateHistoryService kpiUpdateHistoryService;
    private ReviewCycleService reviewCycleService;
    private KPI selectedKpi;
    private List<KpiUpdateHistory> kpiUpdateHistory;
    private List<ReviewCycle> reviewCycles;
    private User loggedInUser;
    private List<ChartDataPoint> chartDataPoints;
    
    // Update form fields
    private Double newValue;
    private String updateComment;
    private String returnPage;

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.kpiUpdateHistoryService = ApplicationContextProvider.getBean(KpiUpdateHistoryService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        
        // Load review cycles
        loadReviewCycles();
        
        // Get KPI ID from request parameter
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String kpiId = params.get("kpiId");
        this.returnPage = params.get("returnPage");
        
        if (kpiId != null) {
            try {
                this.selectedKpi = kpisService.getInstanceByID(kpiId);
                if (this.selectedKpi != null) {
                    loadKpiUpdateHistory();
                    createChartDataFromHistory();
                }
            } catch (Exception e) {
                // Handle invalid ID
                e.printStackTrace();
            }
        }
    }

    private void loadReviewCycles() {
        try {
            this.reviewCycles = reviewCycleService.getAllInstances();
        } catch (Exception e) {
            this.reviewCycles = new ArrayList<>();
        }
    }

    private void loadKpiUpdateHistory() {
        try {
            // Load actual update history from database
            this.kpiUpdateHistory = kpiUpdateHistoryService.getUpdateHistoryByKpi(selectedKpi);
        } catch (Exception e) {
            this.kpiUpdateHistory = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private void createChartDataFromHistory() {
        chartDataPoints = new ArrayList<>();
        
        if (kpiUpdateHistory != null && !kpiUpdateHistory.isEmpty()) {
            // Sort history by date ascending for chart
            List<KpiUpdateHistory> sortedHistory = new ArrayList<>(kpiUpdateHistory);
            sortedHistory.sort((a, b) -> a.getUpdateDate().compareTo(b.getUpdateDate()));
            
            // Create chart data from actual history
            for (KpiUpdateHistory history : sortedHistory) {
                ChartDataPoint point = new ChartDataPoint();
                point.setLabel(getDateLabel(history.getUpdateDate()));
                point.setValue(history.getNewValue());
                chartDataPoints.add(point);
            }
        } else {
            // If no history available, create a single point with current value
            if (selectedKpi.getCurrentValue() != null) {
                ChartDataPoint point = new ChartDataPoint();
                point.setLabel(getDateLabel(new Date()));
                point.setValue(selectedKpi.getCurrentValue());
                chartDataPoints.add(point);
            }
        }
    }
    
    // Simple data class for chart visualization
    public static class ChartDataPoint {
        private String label;
        private Double value;
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    private String getDateLabel(Date date) {
        if (date == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.DAY_OF_MONTH);
    }

    public String updateKpiValue() {
        if (selectedKpi != null && newValue != null) {
            try {
                // Store previous value for history
                Double previousValue = selectedKpi.getCurrentValue();
                
                // Update the KPI current value
                selectedKpi.setCurrentValue(newValue);
                
                // Calculate accomplishment percentage
                if (selectedKpi.getTargetValue() != null && selectedKpi.getTargetValue() > 0) {
                    Double percentage = (newValue * 100.0) / selectedKpi.getTargetValue();
                    selectedKpi.setAccomplishmentPercentage(percentage);
                }
                
                // Save the KPI
                kpisService.saveInstance(selectedKpi);
                
                // Create new update history record
                kpiUpdateHistoryService.createUpdateHistory(selectedKpi, previousValue, newValue, updateComment);
                
                // Reload the update history to show the new record
                loadKpiUpdateHistory();
                
                // Clear form fields
                newValue = null;
                updateComment = null;
                
                // Recreate chart with new data
                createChartDataFromHistory();
                
            } catch (Exception e) {
                // Handle error
                e.printStackTrace();
            }
        }
        return null; // Stay on same page
    }

    public String goBack() {
        if (returnPage != null && !returnPage.isEmpty()) {
            return returnPage + "?faces-redirect=true";
        }
        return "/pages/kpis/KPIView?faces-redirect=true";
    }

    public String getGoalName() {
        if (selectedKpi == null) return "N/A";
        
        if (selectedKpi.getOrganizationGoal() != null) {
            return selectedKpi.getOrganizationGoal().getName();
        }
        if (selectedKpi.getDepartmentGoal() != null) {
            return selectedKpi.getDepartmentGoal().getName();
        }
        if (selectedKpi.getTeamGoal() != null) {
            return selectedKpi.getTeamGoal().getName();
        }
        if (selectedKpi.getIndividualGoal() != null) {
            return selectedKpi.getIndividualGoal().getName();
        }
        
        return "N/A";
    }

    // Backward compatibility: Convert KpiUpdateHistory to the expected format
    public List<KpiUpdate> getKpiUpdates() {
        List<KpiUpdate> updates = new ArrayList<>();
        
        if (kpiUpdateHistory != null) {
            for (KpiUpdateHistory history : kpiUpdateHistory) {
                KpiUpdate update = new KpiUpdate();
                update.setDateUpdated(history.getUpdateDate());
                update.setValue(history.getNewValue());
                update.setUpdatedBy(history.getUpdatedByUser());
                update.setComment(history.getUpdateComment() != null ? history.getUpdateComment() : "No comment");
                updates.add(update);
            }
        }
        
        return updates;
    }

    // Inner class for backward compatibility with existing XHTML
    @Getter
    @Setter
    public static class KpiUpdate implements Serializable {
        private Date dateUpdated;
        private Double value;
        private User updatedBy;
        private String comment;
    }
}
