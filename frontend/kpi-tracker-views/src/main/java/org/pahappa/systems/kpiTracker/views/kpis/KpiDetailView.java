package org.pahappa.systems.kpiTracker.views.kpis;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.ChartSeries;
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
    private KPI selectedKpi;
    private LineChartModel chartModel;
    private List<KpiUpdate> kpiUpdates;
    private User loggedInUser;
    
    // Update form fields
    private Double newValue;
    private String updateComment;
    private String returnPage;

    @PostConstruct
    public void init() {
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        
        // Get KPI ID from request parameter
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String kpiId = params.get("kpiId");
        this.returnPage = params.get("returnPage");
        
        if (kpiId != null) {
            try {
                this.selectedKpi = kpisService.getInstanceByID(kpiId);
                if (this.selectedKpi != null) {
                    loadKpiUpdates();
                    createChartModel();
                }
            } catch (Exception e) {
                // Handle invalid ID
            }
        }
    }

    private void loadKpiUpdates() {
        // Create sample updates for demonstration
        // In a real application, you would load these from the database
        this.kpiUpdates = new ArrayList<>();
        
        // Sample data - replace with actual database query
        Calendar cal = Calendar.getInstance();
        Random random = new Random();
        
        for (int i = 0; i < 5; i++) {
            KpiUpdate update = new KpiUpdate();
            cal.add(Calendar.DAY_OF_MONTH, -30 + (i * 7));
            update.setDateUpdated(cal.getTime());
            update.setValue(Double.valueOf(random.nextInt(100) + 50));
            update.setUpdatedBy(loggedInUser);
            update.setComment("Sample update " + (i + 1));
            kpiUpdates.add(update);
        }
        
        // Sort by date
        kpiUpdates.sort((a, b) -> b.getDateUpdated().compareTo(a.getDateUpdated()));
    }

    private void createChartModel() {
        chartModel = new LineChartModel();
        ChartSeries series = new ChartSeries();
        series.setLabel("KPI Progress");

        // Add sample data points
        Calendar cal = Calendar.getInstance();
        Random random = new Random();
        
        for (int i = 0; i < 6; i++) {
            cal.add(Calendar.MONTH, -1);
            String monthLabel = getMonthLabel(cal.get(Calendar.MONTH));
            series.set(monthLabel, random.nextInt(100) + 20);
        }

        chartModel.addSeries(series);
        chartModel.setTitle("Updates Overtime");
        chartModel.setLegendPosition("ne");
        chartModel.setShowPointLabels(true);
        chartModel.setAnimate(true);
    }

    private String getMonthLabel(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }

    public String updateKpiValue() {
        if (selectedKpi != null && newValue != null) {
            try {
                // Update the KPI current value
                selectedKpi.setCurrentValue(newValue);
                
                // Calculate accomplishment percentage
                if (selectedKpi.getTargetValue() != null && selectedKpi.getTargetValue() > 0) {
                    Double percentage = (newValue * 100.0) / selectedKpi.getTargetValue();
                    selectedKpi.setAccomplishmentPercentage(percentage);
                }
                
                // Save the KPI
                kpisService.saveInstance(selectedKpi);
                
                // Create new update record
                KpiUpdate update = new KpiUpdate();
                update.setDateUpdated(new Date());
                update.setValue(newValue);
                update.setUpdatedBy(loggedInUser);
                update.setComment(updateComment);
                kpiUpdates.add(0, update); // Add to beginning of list
                
                // Clear form fields
                newValue = null;
                updateComment = null;
                
                // Recreate chart with new data
                createChartModel();
                
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

    // Inner class for KPI updates
    @Getter
    @Setter
    public static class KpiUpdate implements Serializable {
        private Date dateUpdated;
        private Double value;
        private User updatedBy;
        private String comment;
    }
}
