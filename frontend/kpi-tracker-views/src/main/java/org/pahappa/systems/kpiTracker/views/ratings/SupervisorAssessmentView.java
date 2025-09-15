package org.pahappa.systems.kpiTracker.views.ratings;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.ratings.SupervisorAssessmentService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.OrgFitCategoryType;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "supervisorAssessment")
@Getter
@Setter
@SessionScoped
public class SupervisorAssessmentView implements Serializable {
    private static final long serialVersionUID = 1L;
    private SupervisorAssessmentService supervisorAssessmentService;
    private transient DepartmentService departmentService;
    private TeamService teamService;
    private StaffService staffService;
    private Department currentDepartment;
    private Team currentTeam;
    private User loggedInUser;
    private Staff loggedInStaff;

    private List<Staff> staffList;

    @PostConstruct
    public void init() {
        this.supervisorAssessmentService = ApplicationContextProvider.getBean(SupervisorAssessmentService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        this.staffList = new ArrayList<>();
        loadDepartment();
        loadTeam();
        loadData();
    }

    public void loadDepartment() {
        if (this.loggedInUser != null && this.loggedInUser.hasRole("Department Lead")) {
            this.currentDepartment = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null && d.getDepartmentHead().equals(this.loggedInUser))
                    .findFirst()
                    .orElse(null);
        }
    }

    public void loadTeam() {
        if (this.loggedInUser != null && this.loggedInUser.hasRole("Team Lead")) {
            this.currentTeam = teamService.getAllInstances()
                    .stream()
                    .filter(d -> d.getTeamHead() != null && d.getTeamHead().equals(this.loggedInUser))
                    .findFirst()
                    .orElse(null);
        }
    }

    public void loadData(){
        this.staffList = new ArrayList<>(); // 👈 reinitialize each time

        if(this.loggedInUser != null && (this.loggedInUser.hasRole("CEO") || this.loggedInUser.hasRole("ROLE_ADMINISTRATOR"))){
            this.loggedInStaff = findStaff(loggedInUser);
            List<Department> departments = departmentService.getAllInstances();
            for(Department department : departments){
                if(department.getDepartmentHead() != null){
                    this.staffList.add(findStaff(department.getDepartmentHead()));
                }

            }
        }
        if(this.loggedInUser != null && this.loggedInUser.hasRole("Department Lead") && this.currentDepartment != null){
            this.loggedInStaff = findStaff(loggedInUser);
            List<Team> teams = findDepartmentTeams(this.currentDepartment);
            for(Team team : teams){
                this.staffList.add(findStaff(team.getTeamHead()));
            }
        }
        if(this.loggedInUser != null && this.loggedInUser.hasRole("Team Lead") && this.currentTeam != null){
            this.loggedInStaff = findStaff(loggedInUser);
            this.staffList.addAll(findStaffByTeam(this.currentTeam));
        }
    }


    public Staff findStaff(User user){
        if (user == null) {
            return null; // or throw a custom exception if this should never happen
        }
        return staffService.searchUniqueByPropertyEqual("user.id", user.getId());
    }

    public List<Team> findDepartmentTeams(Department department){
        Search search = new Search(Team.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("department.id",department.getId())
        );
        return  this.teamService.getInstances(search,0,0);
    }


    public List<Staff> findStaffByTeam(Team team){
        Search search = new Search(Staff.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("team.id",team.getId())
        );
        return staffService.getInstances(search,0,0);
    }



}
