package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "teamMembersView")
@Getter
@Setter
@SessionScoped
public class TeamMembersView implements Serializable {
    private static final long serialVersionUID = 1L;

    private StaffService staffService;
    private TeamService teamService;
    private Team selectedTeam;
    private List<Staff> teamMembers;
    private List<Staff> availableMembers;
    private  List<Staff> selectedMembers = new ArrayList<>();

    private String searchTerm;
    private boolean membersDialogVisible;


    @PostConstruct
    public void init() {
        staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.teamService = ApplicationContextProvider.getBean(TeamService.class);
    }

    public void show(Team team) {
        this.selectedTeam = team;
        if (this.selectedTeam != null) {
            Search search = new Search(Staff.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus",RecordStatus.ACTIVE),
                    Filter.equal("team.id",selectedTeam.getId())
            );
            this.teamMembers = staffService.getInstances(search,0,0);
        } else {
            this.teamMembers = null;
        }
    }

    public String goToTeamMembersView(String id) {
        this.selectedTeam = teamService.getInstanceByID(id);
        if (this.selectedTeam != null) {
            Search search = new Search(Staff.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus",RecordStatus.ACTIVE),
                    Filter.equal("team.id",selectedTeam.getId())
            );
            this.teamMembers = staffService.getInstances(search,0,0);
            show(this.selectedTeam);
            loadAvailableMembers();
        } else {
            this.teamMembers = null;
        }
        membersDialogVisible = true;
        return "TeamMembers?faces-redirect=true";
    }



    public void reloadFilterReset() {

        Search teamMembersSearch = new Search();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            teamMembersSearch.addFilterILike("firstName", "%" + searchTerm + "%");
            teamMembersSearch.addFilterILike("lastName", "%" + searchTerm + "%");
        }


        // Filter and load dataModels based on search/filter criteria
        this.teamMembers = staffService.getInstances(teamMembersSearch, 0, 0);

    }

    public void deleteSelectedTeam(Staff staff) {
        try {
            staffService.deleteInstance(staff);
            reloadFilterReset();
        } catch (org.sers.webutils.model.exception.OperationFailedException e) {
            e.printStackTrace();
        }
    }

    public String cancel() {
        return "/pages/organizationstructure/TeamsView.xhtml";
    }

    public void loadAvailableMembers() {
        if(this.staffService != null){
            Search teamMembersSearch = new Search(Staff.class);
            teamMembersSearch.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("department.id",this.selectedTeam.getDepartment().getId()),
                    Filter.isNull("team")
            );
            this.availableMembers = staffService.getInstances(teamMembersSearch,0,0);
        }
    }

    public void addMembers() throws ValidationFailedException, OperationFailedException {
        if(this.staffService != null && !this.selectedMembers.isEmpty()){
            for(Staff staff : this.selectedMembers){
                staff.setTeam(this.selectedTeam);
                staffService.saveInstance(staff);
                loadAvailableMembers();
                show(this.selectedTeam);
            }
        }
    }

    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }
}
