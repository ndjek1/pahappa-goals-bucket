package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "departmentGoalDetails")
@Getter
@Setter
@SessionScoped
public class DepartmentGoalDetails implements Serializable {
    private DepartmentGoal selectedGoal;
    private TeamGoalService teamGoalService;
    private DepartmentGoalService departmentGoalService;
    private String goalLevel;
    List<TeamGoal> teamGoalGoalList;

    @PostConstruct
    public void init() {
        teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);

    }


    public String prepareForCategory(String id) {
        this.selectedGoal = this.departmentGoalService.getInstanceByID(id);
        this.goalLevel = selectedGoal.getClass().getSimpleName(); // now safe
        loadTeamGoals(); // only load after goal is set
        return "/pages/goals/DepartmentGoalDetails.xhtml?faces-redirect=true";
    }

    public String backToGoals(){
        return "/pages/goals/DepartmentGoalView.xhtml";
    }

    public void loadTeamGoals(){
        Search search = new Search();
        search.addFilterEqual("parent.id",this.selectedGoal.getId());
        this.teamGoalGoalList = this.teamGoalService.getInstances(search,0,0);
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
}
