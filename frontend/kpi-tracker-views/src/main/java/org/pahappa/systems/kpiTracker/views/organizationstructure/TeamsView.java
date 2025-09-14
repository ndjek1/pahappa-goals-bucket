package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "teamsView")
@Getter
@Setter
@SessionScoped
public class TeamsView extends PaginatedTableView<Team, TeamsView,TeamsView> {
    private TeamService teamGoalService;
    private DepartmentService departmentService;
    private StaffService staffService;
    private Search search;
    private Department department;
    private User loggedinUser;


    @PostConstruct
    public void init(){
        this.teamGoalService = ApplicationContextProvider.getBean(TeamService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        reloadFilterReset();
        loadDepartment();
    }
    @Override
    public void reloadFromDB(int i, int i1, Map<String, Object> map) throws Exception {
        super.setDataModels(teamGoalService.getInstances(search,i,i1));
        for(Team team: this.getDataModels()){
            team.setMemberCount(staffService.getMembersByTeam(team).size());
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public List load(int i, int i1, Map map, Map map1) {
        return null;
    }

    @Override
    public void reloadFilterReset(){
        loadDepartment();
        search = new Search(Team.class);
        search.addFilterAnd(
                Filter.equal("recordStatus",RecordStatus.ACTIVE),
                Filter.equal("department.id", this.department.getId())
        );
        super.setTotalRecords(teamGoalService.countInstances(search));
        try{
            super.reloadFilterReset();
        }catch(Exception e){
            UiUtils.ComposeFailure("Error",e.getLocalizedMessage());
        }

    }
    // In TeamsView.java
    public String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "?";
        }
        String[] names = fullName.trim().split("\\s+");
        if (names.length > 1) {
            return ("" + names[0].charAt(0) + names[names.length - 1].charAt(0)).toUpperCase();
        }
        return ("" + names[0].charAt(0)).toUpperCase();
    }

    public void loadDepartment() {
        if (loggedinUser.hasRole("Department Lead")) {
            this.department = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null
                            && d.getDepartmentHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }

    public void deleteClient(Team team) {
        try {
            teamGoalService.deleteInstance(team);
            reloadFilterReset();
        } catch (OperationFailedException e) {
            UiUtils.ComposeFailure("Delete Failed", e.getLocalizedMessage());
        }
    }
}
