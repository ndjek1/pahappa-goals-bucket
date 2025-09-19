package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.IndividualActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpiUpdateHistoryService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.activities.IndividualActivity;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
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
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "individualGoalDetails")
@SessionScoped
@Getter
@Setter
public class IndividualGoalDetails implements Serializable {

    private IndividualGoal selectedGoal;
    private List<KPI> goalKpis;

    private IndividualGoalService individualGoalService;
    private IndividualActivityService individualActivityService;
    private KpisService kpiService;
    private List<IndividualActivity> individualActivities;
    private KpiUpdateHistoryService kpiUpdateHistoryService;
    private IndividualActivity selectedActivity;
    private int progress;
    private String foregroundColor;
    @PostConstruct
    public void init() {
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.individualActivityService = ApplicationContextProvider.getBean(IndividualActivityService.class);
        this.kpiService = ApplicationContextProvider.getBean(KpisService.class);
        this.kpiUpdateHistoryService = ApplicationContextProvider.getBean(KpiUpdateHistoryService.class);
    }

    public String prepareForIndividualGoal(String id) {
        this.selectedGoal = this.individualGoalService.getInstanceByID(id);
        loadKPIs();
        loadActivities();
        return "/pages/goals/IndividualGoalDetails.xhtml?faces-redirect=true";
    }

    public String backToGoals() {
        return "/pages/goals/IndividualGoalsView.xhtml?faces-redirect=true";
    }
    public void loadKPIs(){
        Search search = new  Search(KPI.class);
        search.addFilterAnd(
                Filter.equal("recordStatus", RecordStatus.ACTIVE),
                Filter.equal("individualGoal.id",this.selectedGoal.getId())
        );
        this.goalKpis = kpiService.getInstances(search,0,0);
        if(!this.goalKpis.isEmpty()){
            for(KPI kpi:goalKpis){
                kpi.setProgress(kpiService.getKpiProgress(kpi)*100);
            }
        }
        calculateProgress();
    }

    public void calculateProgress() {
        if (goalKpis == null || goalKpis.isEmpty()) {
            this.progress = 0;
            return;
        }

        double contributionFraction = 0.0;
        for (KPI kpi : goalKpis) {
            double kpiFrac = kpiService.getKpiProgress(kpi);
            double weight = (kpi.getWeight() == null) ? 0.0 : kpi.getWeight();
            contributionFraction += kpiFrac * (weight / 100.0);
        }

        double percent = contributionFraction * 100.0;
        this.progress = (int) Math.round(percent); // Round to nearest int

        // Update color
        this.foregroundColor = new GoalProgressUtil().getProgressColor(this.progress);
    }



    public void loadActivities(){
        Search search = new Search(IndividualActivity.class);
        search.addFilterAnd(
                Filter.equal("recordStatus", RecordStatus.ACTIVE),
                Filter.equal("individualGoal.id",this.selectedGoal.getId())
        );
        this.individualActivities = this.getIndividualActivityService().getInstances(search,0,0);

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
