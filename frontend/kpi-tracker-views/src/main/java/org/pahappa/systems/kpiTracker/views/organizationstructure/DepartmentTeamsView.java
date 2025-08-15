package org.pahappa.systems.kpiTracker.views.organizationstructure;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "departmentTeamsView")
@SessionScoped
@Getter
@Setter
public class DepartmentTeamsView implements Serializable {

    private static final long serialVersionUID = 1L;

    private TeamService teamService;
    private Department selectedDepartment;
    private List<Team> teams;


    private boolean teamsDialogVisible;


    @PostConstruct
    public void init() {
        teamService = ApplicationContextProvider.getBean(TeamService.class);
    }

    public void show(Department department) {
        this.selectedDepartment = department;
        if (department != null) {
            this.teams = teamService.getTeamsByDepartment(department);
        } else {
            this.teams = null;
        }
    }

}