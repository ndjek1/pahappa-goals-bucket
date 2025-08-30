package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.activities.IndividualActivityService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.kpis.KpisService;
import org.pahappa.systems.kpiTracker.models.activities.IndividualActivity;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.kpis.KPI;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
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

    @PostConstruct
    public void init() {
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.individualActivityService = ApplicationContextProvider.getBean(IndividualActivityService.class);
        this.kpiService = ApplicationContextProvider.getBean(KpisService.class);
    }

    public String prepareForIndividualGoal(String id) {
        this.selectedGoal = this.individualGoalService.getInstanceByID(id);
        loadKPIs();
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
    }

    public double getProgress() {
        if (goalKpis == null || goalKpis.isEmpty()) return 0;
        double weightedSum = 0, totalWeight = 0;
        for (KPI kpi : goalKpis) {
            weightedSum += kpi.getProgress() * kpi.getWeight();
            totalWeight += kpi.getWeight();
        }
        return totalWeight > 0 ? weightedSum / totalWeight : 0;
    }

    public void loadActivities(){
        Search search = new Search(IndividualActivity.class);
        search.addFilterAnd(
                Filter.equal("recordStatus", RecordStatus.ACTIVE),
                Filter.equal("individualGoal.id",this.selectedGoal.getId())
        );
        this.individualActivities = this.getIndividualActivityService().getInstances(search,0,0);

    }
}
