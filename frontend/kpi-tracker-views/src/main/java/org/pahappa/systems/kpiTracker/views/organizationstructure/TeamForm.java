package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.TeamGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.TeamGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.security.RoleConstants;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.primefaces.PrimeFaces;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.HashSet;
import java.util.List;

@ManagedBean(name = "teamForm")
@Getter
@Setter
@SessionScoped
public class TeamForm extends DialogForm<Team> {
    private static final long serialVersionUID = 1L;
    private TeamService teamService;
    private DepartmentService departmentService;
    private StaffService staffService;
    private Team team;
    private Department selectedDepartment;
    private User loggedinUser;
    private List<Staff> departmentMembers;
    private List<Staff> selectedTeamMembers;
    private Staff teamLead;
    private transient UserService userService;



    public TeamForm() {
        super(HyperLinks.TEAM_DIALOG, 500, 400);
    }

    @PostConstruct
    public void init() {
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadDepartment();
    }

    @Override
    public void persist() throws Exception {
        if (model.getTeamName() == null) {
            UiUtils.showMessageBox("Missing team name","Goal must have a type.");
            return;
        }


        model.setDepartment(this.selectedDepartment);
        if(this.teamLead != null){
            super.model.setTeamHead(teamLead.getUser());
            teamLead.setTeam(this.model);
            staffService.saveInstance(teamLead);
        }
        Team savedTeam = teamService.saveInstance(super.model);
        if( !this.selectedTeamMembers.isEmpty()){
            for(Staff staff : this.selectedTeamMembers){
                staff.setTeam(savedTeam);
                staffService.saveInstance(staff);
            }
        }

        if (savedTeam != null && this.teamLead != null) {
            this.teamLead.setTeam(savedTeam);
            this.teamLead.getUser().addRole(this.userService.getRoleByRoleName(RoleConstants.ROLE_TEAM_LEAD));
            userService.saveUser(this.teamLead.getUser());
        }
        
    }

    public void loadDepartment() {
        if (loggedinUser.hasRole("Department Lead")) {
            this.selectedDepartment = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null
                            && d.getDepartmentHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }
    // In TeamForm.java

    private void loadDepartmentMembers() {
        if (this.selectedDepartment != null) {
            Search search = new Search(Staff.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("department.id", selectedDepartment.getId())
            );
            if(this.selectedDepartment.getDepartmentHead() != null){
                search.addFilterEqual("user.id", selectedDepartment.getDepartmentHead().getId());
            }

            if (isEditing && super.model != null && super.model.getId() != null) {
                search.addFilterOr(
                        Filter.isNull("team"),
                        Filter.equal("team.id", super.model.getId())
                );
            } else {
                // For new teams, only show unassigned staff.
                search.addFilter(Filter.isNull("team"));
            }
            // --- END OF FIX ---

            this.departmentMembers = this.staffService.getInstances(search, 0, 0);
        } else {
            this.departmentMembers = null;
        }
    }


    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
        if (this.selectedDepartment != null) {
            loadDepartmentMembers(); // reload staff whenever department changes
        }
    }



    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null) {
            isEditing = true;
            loadDepartmentMembers();

            if (super.model.getTeamHead() != null) {
                this.teamLead = staffService.searchUniqueByPropertyEqual(
                        "user.id", super.model.getTeamHead().getId()
                );
            }
        }
    }




    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Team();
    }
}
