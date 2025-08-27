package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "teamGoalDetails")
@Getter
@Setter
@SessionScoped
public class TeamGoalDetails implements Serializable {
    private TeamGoal selectedGoal;
    private TeamGoalService teamGoalService;
    private IndividualGoalService individualGoalService;
    private String goalLevel;
    List<IndividualGoal> individualGoalsList;

    @PostConstruct
    public void init() {
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
    }


    public String prepareForTeamGoal(String id) {
        this.selectedGoal = this.teamGoalService.getInstanceByID(id);
        this.goalLevel = selectedGoal.getClass().getSimpleName(); // now safe
        loadIndividualGoals(); // only load after goal is set
        return "/pages/goals/TeamGoalDetails.xhtml?faces-redirect=true";
    }

    public String backToGoals(){
        return "/pages/goals/TeamGoalView.xhtml";
    }

    public void loadIndividualGoals(){
        Search search = new Search();
        search.addFilterEqual("teamGoal.id",this.selectedGoal.getId());
        this.individualGoalsList = this.individualGoalService.getInstances(search,0,0);
    }

    public void approveIndividualGoal(IndividualGoal individualGoal) throws ValidationFailedException, OperationFailedException {
        if(individualGoal != null){
            individualGoal.setStatus(GoalStatus.APPROVED);
            this.individualGoalService.saveInstance(individualGoal);
            UiUtils.showMessageBox("Team Goal Approved", individualGoal.getName());
        }else {
            UiUtils.showMessageBox("Goal is empty", "You did not select any goal");
        }

    }
}
