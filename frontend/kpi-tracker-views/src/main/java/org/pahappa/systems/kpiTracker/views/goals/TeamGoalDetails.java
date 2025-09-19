package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.TeamActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.activities.TeamActivity;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.utils.GoalProgressUtil;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "teamGoalDetails")
@Getter
@Setter
@SessionScoped
public class TeamGoalDetails implements Serializable {
    private TeamGoal selectedGoal;
    private TeamGoalService teamGoalService;
    private IndividualGoalService individualGoalService;
    private KpisService kpiService;

    private List<IndividualGoal> individualGoalsList, allIndividualGoals;
    private KpiUpdateHistoryService kpiUpdateHistoryService;
    private TeamActivityService teamActivityService;
    private List<TeamActivity> activities;
    private TeamActivity selectedActivity;
    private String foregroundColor;
    private int progress;

    @PostConstruct
    public void init() {
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.kpiUpdateHistoryService = ApplicationContextProvider.getBean(KpiUpdateHistoryService.class);
        this.kpiService = ApplicationContextProvider.getBean(KpisService.class);
        this.teamActivityService = ApplicationContextProvider.getBean(TeamActivityService.class);
    }

    public String prepareForTeamGoal(String id) {
        this.selectedGoal = this.teamGoalService.getInstanceByID(id);
        loadIndividualGoals();
        loadAllIndividualGoals();
        loadActivities();
        calculateProgress(this.selectedGoal,this.individualGoalsList);
        return "/pages/goals/TeamGoalDetails.xhtml?faces-redirect=true";
    }

    public String backToGoals(){
        return "/pages/goals/TeamGoalView.xhtml?faces-redirect=true";
    }

    public void loadIndividualGoals(){
        Search search = new Search(IndividualGoal.class);
        search.addFilterEqual("teamGoal.id", this.selectedGoal.getId());
        search.addFilterEqual("status",GoalStatus.APPROVED);
        this.individualGoalsList = individualGoalService.getInstances(search,0,0);
    }

    public void loadAllIndividualGoals(){
        Search search = new Search(IndividualGoal.class);
        search.addFilterEqual("teamGoal.id", this.selectedGoal.getId());
        search.addFilterEqual("recordStatus",RecordStatus.ACTIVE);
        this.allIndividualGoals = individualGoalService.getInstances(search,0,0);
    }



    public void calculateProgress(TeamGoal teamGoal, List<IndividualGoal> goals) {
        if (goals == null || goals.isEmpty()) {
            return;
        }

        double teamGoalProgress = 0.0;

        // Loop through each individual goal that contributes to the team goal
        for (IndividualGoal individualGoal : goals) {

            // --- Part 1: Calculate the progress of the individual goal ---
            List<KPI> kpis = loadKPIs(individualGoal);
            double individualGoalProgress = 0.0;

            if (!kpis.isEmpty()) {
                for (KPI kpi : kpis) {
                    // Get KPI progress as a fraction (e.g., 0.5 for 50%)
                    double kpiProgress = kpiService.getKpiProgress(kpi);

                    // Get KPI weight as a fraction (e.g., 50 for 50% becomes 0.5)
                    // Assumes getWeight() returns a percentage value like 50.0
                    double kpiWeightAsFraction = kpi.getWeight() / 100.0;

                    // Add the KPI's weighted contribution to the individual goal's progress
                    individualGoalProgress += kpiProgress * kpiWeightAsFraction;
                }
            }
            double goalContributionAsFraction = individualGoal.getContributionWeight() / 100.0;

            teamGoalProgress += individualGoalProgress * goalContributionAsFraction;
        }
        this.progress = (int) Math.round(teamGoalProgress*100); // Round to nearest int

        // Update color
        this.foregroundColor = new GoalProgressUtil().getProgressColor(this.progress);
    }


    public List<KPI> loadKPIs(IndividualGoal goal){
        Search search = new  Search(KPI.class);
        search.addFilterAnd(
                Filter.equal("recordStatus", RecordStatus.ACTIVE),
                Filter.equal("individualGoal.id",goal.getId())
        );
        return kpiService.getInstances(search,0,0);
    }

    public void approveIndividualGoal(IndividualGoal individualGoal) throws ValidationFailedException, OperationFailedException {
        if(individualGoal != null){
            individualGoal.setStatus(GoalStatus.APPROVED);
            this.individualGoalService.saveInstance(individualGoal);
            UiUtils.showMessageBox("Goal Approved", individualGoal.getName());
            loadAllIndividualGoals();
            loadIndividualGoals(); // refresh
        } else {
            UiUtils.showMessageBox("Empty Goal", "No goal selected.");
        }
    }

    public DonutChartModel getProgressDonutModel() {
        DonutChartModel model = new DonutChartModel();
        ChartData data = new ChartData();
        DonutChartDataSet dataSet = new DonutChartDataSet();

        double preciseProgress = getProgress() * 100; // fraction → percent
        BigDecimal bd = new BigDecimal(Double.toString(preciseProgress));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        double progress = bd.doubleValue();
        double remaining = 100 - progress;

        dataSet.setData(Arrays.asList(progress, remaining));
        dataSet.setBackgroundColor(Arrays.asList("#58a73a", "#c92c2c"));

        data.addChartDataSet(dataSet);
        data.setLabels(Arrays.asList("Progress", "Remaining"));

        model.setData(data);
        return model;
    }

    public void loadActivities(){
        if(this.selectedGoal != null){
            this.activities = teamActivityService.searchByPropertyEqual("teamGoal.id",this.selectedGoal.getId(),RecordStatus.ACTIVE);
        }else {
            this.activities = new ArrayList<>();
        }
    }

}

