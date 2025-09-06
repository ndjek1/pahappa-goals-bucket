package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.goals.*;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@ManagedBean(name = "teamGoalForm")
@Getter
@Setter
@SessionScoped
public class TeamGoalForm extends DialogForm<TeamGoal> {
    private static final long serialVersionUID = 1L;
    private TeamGoalService teamGoalService;
    private DepartmentGoalService departmentGoalService;
    private List<DepartmentGoal> departmentGoals;
    private TeamService teamService;
    private Team team;
    private User loggedinUser;

    public TeamGoalForm() {
        super(HyperLinks.TEAM_GOAL_DIALOG, 700, 430);
    }

    @PostConstruct
    public void init() {
        resetModal();
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadDepartment();
        loadDepartmentGoals();
    }

    @Override
    public void persist() throws Exception {
        if (model.getName() == null) {
            UiUtils.showMessageBox("Missing goal name","Goal must have a type.");
            return;
        }
        super.model.setTeam(team);
        super.model.setStatus(GoalStatus.PENDING);
        double percent = calculateRemainingPercentage(model.getParent());
        if( percent<model.getContributionWeight()){
            if(percent<=0){
                UiUtils.showMessageBox("Contribution weight is reached", "Can no longer  contribute to that parent goal");
            }else {
                UiUtils.showMessageBox("Contribution weight to high", "Can only contribute " + calculateRemainingPercentage(this.model.getParent()) + " to this selected goal");
                return;
            }
        }
        teamGoalService.saveInstance(super.model);
        resetModal();
        hide();
    }

    public void loadDepartment() {
        if (loggedinUser.hasRole("Team Lead")) {
            this.team = teamService.getAllInstances()
                    .stream()
                    .filter(d -> d.getTeamHead() != null
                            && d.getTeamHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }

    public void loadDepartmentGoals(){
        Search search = new Search();
        search.addFilterEqual("status", GoalStatus.APPROVED);
        this.departmentGoals = this.departmentGoalService.getInstances(search,0,0);
    }
    public double calculateRemainingPercentage(DepartmentGoal departmentGoal){
        double total = 0.0;
        double remaining = 0.0;

        if(departmentGoal != null){
            Search search = new Search(TeamGoal.class);
            search.addFilterEqual("parent.id", departmentGoal.getId());
            List<TeamGoal> goals = this.teamGoalService.getInstances(search,0,0);
            for(TeamGoal goal: goals){
                total += goal.getContributionWeight();
                remaining = 100 - total;
            }
            return remaining;
        }else {
            return 0.0;
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new TeamGoal();
    }
}
