package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private List<IndividualGoal> individualGoalsList;
    private KpiUpdateHistoryService kpiUpdateHistoryService;

    @PostConstruct
    public void init() {
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.kpiUpdateHistoryService = ApplicationContextProvider.getBean(KpiUpdateHistoryService.class);
        this.kpiService = ApplicationContextProvider.getBean(KpisService.class);
    }

    public String prepareForTeamGoal(String id) {
        this.selectedGoal = this.teamGoalService.getInstanceByID(id);
        loadIndividualGoals();
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

    /**
     * Calculate team goal progress based on its individual goals
     */
    public double getProgress() {
        return calculateProgress(selectedGoal, individualGoalsList);
    }


    public double calculateProgress(TeamGoal teamGoal, List<IndividualGoal> goals) {
        if (goals == null || goals.isEmpty()) return 0;

        double weightedSum = 0, totalWeight = 0;

        for (IndividualGoal goal : goals) {
            // Explicitly load KPIs using service
            List<KPI> kpis = loadKPIs(goal);

            double goalProgress = 0;
            double kpiWeightedSum = 0, kpiTotalWeight = 0;

            for (KPI kpi : kpis) {
                kpiWeightedSum += this.getKpiProgress(kpi) * kpi.getWeight();
                kpiTotalWeight += kpi.getWeight();
            }

            if (kpiTotalWeight > 0) {
                goalProgress = kpiWeightedSum / kpiTotalWeight;
            }

            double weight = goal.getContributionWeight() > 0 ? goal.getContributionWeight() : 1;
            weightedSum += goalProgress * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? weightedSum / totalWeight : 0;
    }

    public List<KPI> loadKPIs(IndividualGoal goal){
        Search search = new  Search(KPI.class);
        search.addFilterAnd(
                Filter.equal("recordStatus", RecordStatus.ACTIVE),
                Filter.equal("individualGoal.id",goal.getId())
        );
        return kpiService.getInstances(search,0,0);
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



    public void approveIndividualGoal(IndividualGoal individualGoal) throws ValidationFailedException, OperationFailedException {
        if(individualGoal != null){
            individualGoal.setStatus(GoalStatus.APPROVED);
            this.individualGoalService.saveInstance(individualGoal);
            UiUtils.showMessageBox("Goal Approved", individualGoal.getName());
            loadIndividualGoals(); // refresh
        } else {
            UiUtils.showMessageBox("Empty Goal", "No goal selected.");
        }
    }

    public DonutChartModel getProgressDonutModel() {
        DonutChartModel model = new DonutChartModel();
        ChartData data = new ChartData();

        DonutChartDataSet dataSet = new DonutChartDataSet();

        double preciseProgress = getProgress();
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
}

