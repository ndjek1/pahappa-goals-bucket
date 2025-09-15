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
        if (departmentGoalList == null || departmentGoalList.isEmpty()) return 0;

        double weightedSum = 0;
        double totalWeight = 0;

        for (DepartmentGoal deptGoal : departmentGoalList) {
            double deptProgress = calculateDepartmentGoalProgress(deptGoal);
            double weight = deptGoal.getContributionWeight() > 0 ? deptGoal.getContributionWeight() : 1;

            weightedSum += deptProgress * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? weightedSum / totalWeight : 0;
    }

    public double calculateDepartmentGoalProgress(DepartmentGoal departmentGoal) {
        double weightedSum = 0;
        double totalWeight = 0;

//        // 1. KPIs directly under department
//        Search kpiSearch = new Search(KPI.class);
//        kpiSearch.addFilterEqual("departmentGoal.id", departmentGoal.getId());
//        List<KPI> deptKPIs = kpisService.getInstances(kpiSearch,0,0);
//        if (deptKPIs != null && !deptKPIs.isEmpty()) {
//            double kpiWeightedSum = 0, kpiTotalWeight = 0;
//            for (KPI kpi : deptKPIs) {
//                double w = kpi.getWeight() != null ? kpi.getWeight() : 1;
//                kpiWeightedSum += getKpiProgress(kpi) * w;
//                kpiTotalWeight += w;
//            }
//            if (kpiTotalWeight > 0) {
//                weightedSum += kpiWeightedSum / kpiTotalWeight; // treat as one bucket
//                totalWeight += 1;
//            }
//        }

        Search search = new Search(TeamGoal.class);
        search.addFilterEqual("departmentGoal.id", departmentGoal.getId());
        List<TeamGoal> teamGoals = teamGoalService.getInstances(search,0,0);
        if (teamGoals != null && !teamGoals.isEmpty()) {
            for (TeamGoal teamGoal : teamGoals) {
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
                    kpiWeightedSum += getKpiProgress(kpi) * (kpi.getWeight() != null ? kpi.getWeight() : 1);
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
