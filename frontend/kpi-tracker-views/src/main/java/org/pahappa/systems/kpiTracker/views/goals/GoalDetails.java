package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.goals.*;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.models.kpis.KpiUpdateHistory;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.donut.DonutChartModel;
import org.primefaces.model.charts.donut.DonutChartDataSet;
import org.sers.webutils.client.views.presenters.ViewPath;
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


@ManagedBean(name = "goalDetailsView")
@Getter
@Setter
@SessionScoped
@ViewPath(path=HyperLinks.GOAL_VIEW)
public class GoalDetails implements Serializable {
    private OrganizationGoal selectedGoal;
    private OrganizationGoalService organizationGoalService;
    private DepartmentGoalService departmentGoalService;
    private IndividualGoalService individualGoalService;
    private TeamGoalService teamGoalService;
    private KpisService kpisService;
    private String goalLevel;
    List<DepartmentGoal> departmentGoalList;
    private KpiUpdateHistoryService kpiUpdateHistoryService;

    @PostConstruct
    public void init() {
        organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.kpisService = ApplicationContextProvider.getBean(KpisService.class);
        this.kpiUpdateHistoryService = ApplicationContextProvider.getBean(KpiUpdateHistoryService.class);
        this.goalLevel = selectedGoal != null ? selectedGoal.getClass().getSimpleName() : "";
    }


    public String prepareForCategory(String id) {
        this.selectedGoal = this.organizationGoalService.getInstanceByID(id);
        this.goalLevel = selectedGoal.getClass().getSimpleName(); // now safe
        loadDepartmentGoals(); // only load after goal is set
        return "/pages/goals/goalDetails.xhtml?faces-redirect=true";
    }

    public String backToGoals(){
        return "/pages/goals/OrganizationGoalsView.xhtml";
    }

    public void loadDepartmentGoals(){
        Search search = new Search();
        search.addFilterEqual("organizationGoal.id",this.selectedGoal.getId());
        this.departmentGoalList = this.departmentGoalService.getInstances(search,0,0);
    }

    public void approveDepartmentGoal(DepartmentGoal departmentGoal) throws ValidationFailedException, OperationFailedException {
        if(departmentGoal != null){
            departmentGoal.setStatus(GoalStatus.APPROVED);
            this.departmentGoalService.saveInstance(departmentGoal);
            UiUtils.showMessageBox("Department Goal Approved", departmentGoal.getName());
        }else {
            UiUtils.showMessageBox("Goal is empty", "You did not select any goal");
        }

    }

    public double calculateOrganizationGoalProgress() {
        if (departmentGoalList == null || departmentGoalList.isEmpty()) {
            return 0.0;
        }

        double organizationProgress = 0.0;

        for (DepartmentGoal deptGoal : departmentGoalList) {
            // Step 1: Calculate the progress of this specific department goal.
            double departmentProgress = calculateDepartmentGoalProgress(deptGoal); // This will be a fraction e.g., 0.50

            // Step 2: Get this department goal's contribution weight to the organization goal as a fraction.
            // Assumes getContributionWeight() returns a percentage value like 60.0 for 60%.
            double departmentContributionAsFraction = deptGoal.getContributionWeight() / 100.0; // e.g., 60.0 -> 0.60

            // Step 3: Add its weighted contribution to the organization's total progress.
            organizationProgress += departmentProgress * departmentContributionAsFraction;
        }

        // The final progress is the sum of the direct contributions.
        return organizationProgress;
    }

    public double calculateDepartmentGoalProgress(DepartmentGoal departmentGoal) {
        // Load the team goals for this specific department goal
        Search teamSearch = new Search(TeamGoal.class);
        teamSearch.addFilterEqual("departmentGoal.id", departmentGoal.getId());
        teamSearch.addFilterEqual("status", GoalStatus.APPROVED); // Ensure we only count approved goals
        List<TeamGoal> teamGoals = teamGoalService.getInstances(teamSearch, 0, 0);

        if (teamGoals == null || teamGoals.isEmpty()) {
            return 0.0;
        }

        double departmentProgress = 0.0;

        for (TeamGoal teamGoal : teamGoals) {
            // Get the progress of the team goal first
            double teamProgress = calculateTeamGoalProgress(teamGoal);

            // Get the team goal's contribution weight as a fraction
            double teamContributionAsFraction = teamGoal.getContributionWeight() / 100.0;

            // Add its weighted contribution to the department's total progress
            departmentProgress += teamProgress * teamContributionAsFraction;
        }

        return departmentProgress;
    }

    private double calculateTeamGoalProgress(TeamGoal teamGoal) {
        // Load the approved individual goals for this specific team goal
        Search individualSearch = new Search(IndividualGoal.class);
        individualSearch.addFilterEqual("teamGoal.id", teamGoal.getId());
        individualSearch.addFilterEqual("status", GoalStatus.APPROVED);
        List<IndividualGoal> individualGoals = this.individualGoalService.getInstances(individualSearch, 0, 0);

        if (individualGoals == null || individualGoals.isEmpty()) {
            return 0.0;
        }

        double teamProgress = 0.0;

        for (IndividualGoal individualGoal : individualGoals) {

            // --- Part 1: Calculate the progress of the individual goal from its KPIs ---
            Search kpiSearch = new Search(KPI.class);
            kpiSearch.addFilterEqual("individualGoal.id", individualGoal.getId());
            List<KPI> kpis = this.kpisService.getInstances(kpiSearch, 0, 0);

            double individualGoalProgress = 0.0;
            if (!kpis.isEmpty()) {
                for (KPI kpi : kpis) {
                    // Convert the KPI's progress (which is 0-100) to a fraction
                    double kpiProgressAsFraction = getKpiProgress(kpi) / 100.0;

                    // Convert the KPI's weight (e.g., 50 for 50%) to a fraction
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

        double preciseProgress = calculateOrganizationGoalProgress();
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
