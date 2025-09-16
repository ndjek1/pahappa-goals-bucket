package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.DepartmentActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.activities.DepartmentActivity;
import org.pahappa.systems.kpiTracker.models.goals.*;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "departmentGoalDetails")
@Getter
@Setter
@SessionScoped
public class DepartmentGoalDetails implements Serializable {
    private DepartmentGoal selectedGoal;
    private TeamGoalService teamGoalService;
    private KpisService kpisService;

    private DepartmentGoalService departmentGoalService;
    private DepartmentActivityService departmentActivityService;
    private IndividualGoalService individualGoalService;
    private String goalLevel;
    List<TeamGoal> teamGoalGoalList;
    private List<KPI> kpisList;
    private List<DepartmentActivity> departmentActivityList;
    private KpiUpdateHistoryService kpiUpdateHistoryService;

    @PostConstruct
    public void init() {
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.departmentActivityService = ApplicationContextProvider.getBean(DepartmentActivityService.class);
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.kpiUpdateHistoryService = ApplicationContextProvider.getBean(KpiUpdateHistoryService.class);

    }


    public String prepareForCategory(String id) {
        this.selectedGoal = this.departmentGoalService.getInstanceByID(id);
        this.goalLevel = selectedGoal.getClass().getSimpleName(); // now safe

        loadTeamGoals(); // only load after goal is set
        loadKPIs();
        loadActivities();
        return "/pages/goals/DepartmentGoalDetails.xhtml";
    }

    public String backToGoals(){
        return "/pages/goals/DepartmentGoalView.xhtml";
    }

    public void loadTeamGoals(){
        Search search = new Search();
        search.addFilterEqual("departmentGoal.id",this.selectedGoal.getId());
        this.teamGoalGoalList = this.teamGoalService.getInstances(search,0,0);
    }

    public void approveTeamGoal(TeamGoal teamGoal) throws ValidationFailedException, OperationFailedException {
        if(teamGoal != null){
            teamGoal.setStatus(GoalStatus.APPROVED);
            this.teamGoalService.saveInstance(teamGoal);
            UiUtils.showMessageBox("Team Goal Approved", teamGoal.getName());
        }else {
            UiUtils.showMessageBox("Goal is empty", "You did not select any goal");
        }

    }

    public void loadKPIs(){
        if(this.kpisService != null){
            Search search = new Search(KPI.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("departmentGoal.id",this.selectedGoal.getId())
            );
            this.kpisList = this.kpisService.getInstances(search,0,0);
        }
    }

    public void loadActivities(){
        Search search = new Search(DepartmentActivity.class);
        search.addFilterAnd(
                Filter.equal("recordStatus", RecordStatus.ACTIVE),
                Filter.equal("departmentGoal.id",this.selectedGoal.getId())
        );
        this.departmentActivityList = this.departmentActivityService.getInstances(search,0,0);

    }

    /**
     * Calculates the overall progress of the selected department goal.
     * It sums the direct percentage contributions from its underlying team goals.
     * @return The progress of the department goal as a fraction (e.g., 0.25 for 25%).
     */
    public double calculateProgress() {
        double departmentProgress = 0.0;

        if (this.teamGoalGoalList != null && !this.teamGoalGoalList.isEmpty()) {
            for (TeamGoal teamGoal : this.teamGoalGoalList) {
                // Step 1: Calculate the progress of this specific team goal.
                double teamProgress = calculateTeamGoalProgress(teamGoal); // This will be a fraction e.g., 0.50

                // Step 2: Get this team goal's contribution weight to the department goal as a fraction.
                // Assumes getContributionWeight() returns a percentage value like 70.0 for 70%.
                double teamContributionAsFraction = teamGoal.getContributionWeight() / 100.0; // e.g., 70.0 -> 0.70

                // Step 3: Add its weighted contribution to the department's total progress.
                departmentProgress += teamProgress * teamContributionAsFraction;
            }
        }

        // Note: We are not dividing by the sum of weights here.
        return departmentProgress;
    }

    /**
     * Helper method to calculate a single team goal’s progress based on the
     * direct percentage contribution of its Individual Goals.
     * This follows the exact same logic as the previous solution.
     * @param teamGoal The TeamGoal to calculate progress for.
     * @return The progress of the team goal as a fraction (e.g., 0.5 for 50%).
     */
    private double calculateTeamGoalProgress(TeamGoal teamGoal) {
        // Load the approved individual goals for this specific team goal
        Search individualSearch = new Search(IndividualGoal.class);
        individualSearch.addFilterEqual("teamGoal.id", teamGoal.getId());
        individualSearch.addFilterEqual("status", GoalStatus.APPROVED); // Ensure we only count approved goals
        List<IndividualGoal> individualGoals = this.individualGoalService.getInstances(individualSearch, 0, 0);

        if (individualGoals == null || individualGoals.isEmpty()) {
            return 0.0;
        }

        double teamProgress = 0.0;

        // Loop through each individual goal that contributes to the team goal
        for (IndividualGoal individualGoal : individualGoals) {

            // --- Part 1: Calculate the progress of the individual goal ---
            Search kpiSearch = new Search(KPI.class);
            kpiSearch.addFilterEqual("individualGoal.id", individualGoal.getId());
            List<KPI> kpis = this.kpisService.getInstances(kpiSearch, 0, 0);

            double individualGoalProgress = 0.0;
            if (!kpis.isEmpty()) {
                for (KPI kpi : kpis) {
                    // We need to use your getKpiProgress method, but it returns a percentage (0-100).
                    // We must convert it to a fraction for our calculation.
                    double kpiProgressAsFraction = getKpiProgress(kpi) / 100.0; // e.g., 50.0 -> 0.5

                    // Get KPI weight as a fraction (e.g., 50 for 50% becomes 0.5)
                    double kpiWeightAsFraction = kpi.getWeight() / 100.0;

                    individualGoalProgress += kpiProgressAsFraction * kpiWeightAsFraction;
                }
            }

            // --- Part 2: Add this individual goal's contribution to the team goal ---
            double goalContributionAsFraction = individualGoal.getContributionWeight() / 100.0;
            teamProgress += individualGoalProgress * goalContributionAsFraction;
        }

        return teamProgress;
    }

    public DonutChartModel getProgressDonutModel() {
        DonutChartModel model = new DonutChartModel();
        ChartData data = new ChartData();

        DonutChartDataSet dataSet = new DonutChartDataSet();

        double preciseProgress = calculateProgress();
        BigDecimal bd = new BigDecimal(Double.toString(preciseProgress));
        bd = bd.setScale(1, RoundingMode.HALF_UP); // Set scale to 1 for one decimal place
        double progress = bd.doubleValue();
        double remaining = 100 - progress;

        dataSet.setData(Arrays.asList(progress, remaining));
        dataSet.setBackgroundColor(Arrays.asList("#58a73a", "#c92c2c"));

        data.addChartDataSet(dataSet);
        data.setLabels(Arrays.asList("Progress", "Remaining"));

        model.setData(data);
        return model;
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

}
