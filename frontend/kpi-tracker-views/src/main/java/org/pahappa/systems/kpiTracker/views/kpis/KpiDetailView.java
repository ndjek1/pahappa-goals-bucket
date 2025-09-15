package org.pahappa.systems.kpiTracker.views.kpis;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.Frequency;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
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
    private LineChartModel lineModel;

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

        loadReviewCycles();

        // Get KPI ID from request parameter
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String kpiId = params.get("kpiId");
        this.returnPage = params.get("returnPage");

        if (kpiId != null) {
            try {
                this.selectedKpi = kpisService.getInstanceByID(kpiId);
                if (this.selectedKpi != null) {
                    loadKpiHistory();
                    createChartDataFromHistory();
                }
            } catch (Exception e) {
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

    private void loadKpiHistory() {
        if (selectedKpi != null) {
            this.kpiUpdateHistory = kpiUpdateHistoryService.getUpdateHistoryByKpi(selectedKpi);
        } else {
            this.kpiUpdateHistory = new ArrayList<>();
        }
    }


    private String getFrequencyLabel(Date date, Frequency frequency) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        switch (frequency) {
            case DAILY:
                return cal.get(Calendar.DAY_OF_MONTH) + " "
                        + new java.text.DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
            case WEEKLY:
                int week = cal.get(Calendar.WEEK_OF_YEAR);
                return "Week " + week + " (" + cal.get(Calendar.YEAR) + ")";
            case MONTHLY:
                return new java.text.DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)]
                        + " " + cal.get(Calendar.YEAR);
            case QUARTERLY:
                int quarter = (cal.get(Calendar.MONTH) / 3) + 1;
                return "Q" + quarter + " " + cal.get(Calendar.YEAR);
            case BIANNUALLY:
                return (cal.get(Calendar.MONTH) < 6 ? "H1 " : "H2 ") + cal.get(Calendar.YEAR);
            case ANNUALLY:
                return String.valueOf(cal.get(Calendar.YEAR));
            default:
                return date.toString();
        }
    }



    // === Chart data class ===
    public static class ChartDataPoint {
        private String label;
        private Double value;
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    private void createChartDataFromHistory() {
        chartDataPoints = new ArrayList<>();
        lineModel = new LineChartModel();

        if (kpiUpdateHistory != null && !kpiUpdateHistory.isEmpty()) {
            List<KpiUpdateHistory> sortedHistory = new ArrayList<>(kpiUpdateHistory);
            sortedHistory.sort(Comparator.comparing(KpiUpdateHistory::getDateCreated));

            ChartData data = new ChartData();
            LineChartDataSet dataSet = new LineChartDataSet();

            List<Object> values = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (KpiUpdateHistory history : sortedHistory) {
                labels.add(getFrequencyLabel(history.getDateCreated(), selectedKpi.getFrequency()));
                values.add(history.getValue());
            }

            dataSet.setLabel("Progress");
            dataSet.setData(values);
            dataSet.setFill(true);
            dataSet.setBorderColor("rgb(75, 192, 192)");


            data.addChartDataSet(dataSet);
            data.setLabels(labels);

            lineModel.setData(data);

        } else if (selectedKpi.getCurrentValue() != null) {
            ChartData data = new ChartData();
            LineChartDataSet dataSet = new LineChartDataSet();

            List<Object> values = Collections.singletonList(selectedKpi.getCurrentValue());
            List<String> labels = Collections.singletonList(getFrequencyLabel(new Date(), selectedKpi.getFrequency()));

            dataSet.setLabel("Progress");
            dataSet.setData(values);
            dataSet.setFill(false);
            dataSet.setBorderColor("rgb(75, 192, 192)");

            data.addChartDataSet(dataSet);
            data.setLabels(labels);

            lineModel.setData(data);
        }
    }



    public String updateKpiValue() {
        if (selectedKpi != null && newValue != null) {
            try {
                // Check last update against frequency
                if (!canUpdateBasedOnFrequency()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_WARN,
                                    "Update Not Allowed",
                                    "KPI can only be updated once per " + selectedKpi.getFrequency().getDisplayName()));
                    return null;
                }

                selectedKpi.setCurrentValue(newValue);
                kpisService.saveInstance(selectedKpi);

                // Save history
                KpiUpdateHistory history = new KpiUpdateHistory();
                history.setKpi(selectedKpi);
                history.setValue(newValue);
                history.setUpdateDate(new Date());
                history.setComment(updateComment);
                history.setChangedBy(loggedInUser);
                KpiUpdateHistory history1 = kpiUpdateHistoryService.saveInstance(history);
                if(history1 != null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new javax.faces.application.FacesMessage(javax.faces.application.FacesMessage.SEVERITY_WARN,
                                    "Success",
                                    "KPI Value updated successfully "));
                }
                loadKpiHistory();
                createChartDataFromHistory();

                newValue = null;
                updateComment = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private boolean canUpdateBasedOnFrequency() {
        if (kpiUpdateHistory == null || kpiUpdateHistory.isEmpty()) {
            return true; // no restriction if no history
        }

        // get most recent history
        KpiUpdateHistory lastUpdate = Collections.max(kpiUpdateHistory,
                Comparator.comparing(KpiUpdateHistory::getDateCreated));

        Calendar cal = Calendar.getInstance();
        cal.setTime(lastUpdate.getUpdateDate());

        Calendar now = Calendar.getInstance();

        switch (selectedKpi.getFrequency()) {
            case DAILY:
                return now.get(Calendar.DAY_OF_YEAR) != cal.get(Calendar.DAY_OF_YEAR)
                        || now.get(Calendar.YEAR) != cal.get(Calendar.YEAR);
            case WEEKLY:
                return now.get(Calendar.WEEK_OF_YEAR) != cal.get(Calendar.WEEK_OF_YEAR)
                        || now.get(Calendar.YEAR) != cal.get(Calendar.YEAR);
            case MONTHLY:
                return now.get(Calendar.MONTH) != cal.get(Calendar.MONTH)
                        || now.get(Calendar.YEAR) != cal.get(Calendar.YEAR);
            case QUARTERLY:
                return (now.get(Calendar.MONTH) / 3) != (cal.get(Calendar.MONTH) / 3)
                        || now.get(Calendar.YEAR) != cal.get(Calendar.YEAR);
            case BIANNUALLY:
                return (now.get(Calendar.MONTH) < 6 ? 0 : 1) != (cal.get(Calendar.MONTH) < 6 ? 0 : 1)
                        || now.get(Calendar.YEAR) != cal.get(Calendar.YEAR);
            case ANNUALLY:
                return now.get(Calendar.YEAR) != cal.get(Calendar.YEAR);
            default:
                return true;
        }
    }

    public double getKpiProgress(KPI kpi) {
        if(kpi != null){
            Search search = new  Search(KpiUpdateHistory.class);
            search.addFilterEqual("kpi.id",kpi.getId());
            List<KpiUpdateHistory> updateHistories = kpiUpdateHistoryService.getUpdateHistoryByKpi(kpi);
            if (kpi.getTargetValue() == null || kpi.getTargetValue() <= 0) {
                return 0;
            }
            if (kpi.getCurrentValue() == null) {
                return 0;
            }
            double currentValue = 0.0;
            for(KpiUpdateHistory updateHistory : updateHistories){
                currentValue += updateHistory.getValue();
            }
            return Math.round(((currentValue / kpi.getTargetValue()) * 100) * 100.0) / 100.0;
        }else {
            return 0;
        }
    }

    public String goBack() {
        if (returnPage != null && !returnPage.isEmpty()) {
            return returnPage + "?faces-redirect=true";
        }
        return "/pages/kpis/KPIView?faces-redirect=true";
    }

    public String getGoalName() {
        if (selectedKpi == null) return "N/A";
        if (selectedKpi.getOrganizationGoal() != null) return selectedKpi.getOrganizationGoal().getName();
        if (selectedKpi.getDepartmentGoal() != null) return selectedKpi.getDepartmentGoal().getName();
        if (selectedKpi.getTeamGoal() != null) return selectedKpi.getTeamGoal().getName();
        if (selectedKpi.getIndividualGoal() != null) return selectedKpi.getIndividualGoal().getName();
        return "N/A";
    }
}
