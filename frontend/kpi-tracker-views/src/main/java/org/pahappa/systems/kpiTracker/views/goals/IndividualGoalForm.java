package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.IndividualGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.IndividualGoal;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
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

@ManagedBean(name = "individualGoalForm")
@Getter
@Setter
@SessionScoped
public class IndividualGoalForm extends DialogForm<IndividualGoal> {
    private static final long serialVersionUID = 1L;
    private TeamGoalService teamGoalService;
    private DepartmentGoalService departmentGoalService;
    private IndividualGoalService individualGoalService;
    private List<DepartmentGoal> departmentGoals;
    private List<TeamGoal> teamGoals;
    private TeamGoal selectedTeamGoal;
    private DepartmentGoal selectedDepartmentGoal;
    private TeamService teamService;
    private Team team;
    private StaffService staffService;
    private User loggedinUser;
    private Staff loggedinStaff;

    public IndividualGoalForm() {
        super(HyperLinks.INDIVIDUAL_GOAL_DIALOG, 500, 400);
    }

    @PostConstruct
    public void init() {
        this.teamGoalService = ApplicationContextProvider.getBean(TeamGoalService.class);
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.individualGoalService = ApplicationContextProvider.getBean(IndividualGoalService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadTeam();
        loadDepartmentGoals();
    }

    @Override
    public void persist() throws Exception {
        if (model.getName() == null) {
            UiUtils.showMessageBox("Missing goal name", "Goal must have a type.");
            return;
        }
        if(this.selectedTeamGoal != null) {
            model.setTeamGoal(this.selectedTeamGoal);
        }
        if(this.selectedDepartmentGoal != null) {
            model.setParent(this.selectedDepartmentGoal);
        }
        model.setStaff(this.loggedinStaff);
        individualGoalService.saveInstance(super.model);
    }

    public void loadTeam() {

        this.loggedinStaff = staffService.searchUniqueByPropertyEqual("user.id", loggedinUser.getId());

        if (this.loggedinStaff != null) {
            this.team = this.loggedinStaff.getTeam();  // directly from staff
            if (this.team != null) {
                loadTeamGoals();
            }
        } else {
            this.team = null; // user is not assigned to a team
        }
    }


    public void loadDepartmentGoals() {
        Search search = new Search();
        search.addFilterEqual("status", GoalStatus.APPROVED);
        this.departmentGoals = this.departmentGoalService.getInstances(search, 0, 0);
    }

    public void loadTeamGoals() {
        Search search = new Search();
        search.addFilterAnd(
                Filter.equal("team.id", this.team.getId()),
                Filter.equal("status", GoalStatus.APPROVED)
        );
        this.teamGoals = this.teamGoalService.getInstances(search, 0, 0);
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new IndividualGoal();
    }

}
