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

    public double calculateProgress() {
        double weightedSum = 0, totalWeight = 0;

//        // 1. KPIs directly under Department Goal
//        if (kpisList != null && !kpisList.isEmpty()) {
//            double kpiWeightedSum = 0, kpiTotalWeight = 0;
//            for (KPI kpi : kpisList) {
//                kpiWeightedSum += kpi.getProgress() * (kpi.getWeight() != null ? kpi.getWeight() : 1);
//                kpiTotalWeight += (kpi.getWeight() != null ? kpi.getWeight() : 1);
//            }
//            if (kpiTotalWeight > 0) {
//                double kpiProgress = kpiWeightedSum / kpiTotalWeight;
//                // Treat all KPIs as one "bucket" with weight 1
//                weightedSum += kpiProgress;
//                totalWeight += 1;
//            }
//        }

        // 2. Progress from Team Goals
        if (teamGoalGoalList != null && !teamGoalGoalList.isEmpty()) {
            for (TeamGoal teamGoal : teamGoalGoalList) {
                // Get progress via TeamGoalDetails-style calculation
                double teamProgress = calculateTeamGoalProgress(teamGoal);
                double weight = teamGoal.getContributionWeight() > 0 ? teamGoal.getContributionWeight() : 1;

                weightedSum += teamProgress * weight;
                totalWeight += weight;
            }
        }

        return totalWeight > 0 ? weightedSum / totalWeight : 0;
    }

    /**
     * Helper: calculate a team goal’s progress based on its Individual Goals & KPIs
     */
    private double calculateTeamGoalProgress(TeamGoal teamGoal) {
        // Reload individual goals for this team goal
        Search search = new Search(IndividualGoal.class);
        search.addFilterEqual("teamGoal.id", teamGoal.getId());
        List<IndividualGoal> individualGoals = this.individualGoalService.getInstances(search, 0, 0);

        if (individualGoals == null || individualGoals.isEmpty()) return 0;

        double weightedSum = 0, totalWeight = 0;
        for (IndividualGoal goal : individualGoals) {
            // Explicitly load KPIs for this goal (avoid lazy init error)
            Search kpiSearch = new Search(KPI.class);
            kpiSearch.addFilterEqual("individualGoal.id", goal.getId());
            List<KPI> kpis = this.kpisService.getInstances(kpiSearch, 0, 0);

            double goalProgress = 0;
            if (kpis != null && !kpis.isEmpty()) {
                double kpiWeightedSum = 0, kpiTotalWeight = 0;
                for (KPI kpi : kpis) {
                    kpiWeightedSum += this.getKpiProgress(kpi) * (kpi.getWeight() != null ? kpi.getWeight() : 1);
                    kpiTotalWeight += (kpi.getWeight() != null ? kpi.getWeight() : 1);
                }
                goalProgress = kpiTotalWeight > 0 ? kpiWeightedSum / kpiTotalWeight : 0;
            }

            double weight = goal.getContributionWeight() > 0 ? goal.getContributionWeight() : 1;
            weightedSum += goalProgress * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? weightedSum / totalWeight : 0;
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
