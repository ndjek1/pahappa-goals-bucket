package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
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

    public String show(Department department) {
        this.selectedDepartment = department;
        if (department != null) {
            Search search = new Search(Team.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("department.id", department.getId())
            );
            this.teams = teamService.getInstances(search,0,0);
        } else {
            this.teams = null;
        }
        return "/pages/organizationstructure/DepartmentTeams.xhtml";
    }
    public String cancel() {
        return "/pages/organizationstructure/OrganizationStructure.xhtml";
    }

    public void deleteTeam(Team team) {
        try {
            teamService.deleteInstance(team);
            show(this.selectedDepartment);
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }

}