package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.*;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "departmentsView")
@Getter
@Setter
@SessionScoped
public class OrganizationStructureView implements Serializable {

    private static final long serialVersionUID = 1L;

    private DepartmentService departmentService;
    private TeamService teamService;
    private UserService userService;

    private List<RecordStatus> recordStatusList;

    private int totalDepartments;
    private int totalTeams;
    private int totalMembers;
    private int activeDepartments;
    private int activeTeams;

    private String searchTerm;
    private RecordStatus selectedStatus;
    private Date createdFrom, createdTo;

    private List<Department> departmentModels;
    private List<Team> teamModels;
    private String dataEmptyMessage = "No departments found.";

    @PostConstruct
    public void init() {
        departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        teamService = ApplicationContextProvider.getBean(TeamService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);

        this.recordStatusList = Arrays.asList(RecordStatus.values());

        reloadFilterReset();
    }

    public void reloadFilterReset() {

        Search allDepartmentsSearch = new Search();
        this.totalDepartments = departmentService.countInstances(allDepartmentsSearch);

        Search activeDepartmentSearch = new Search();
        activeDepartmentSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.activeDepartments = departmentService.countInstances(activeDepartmentSearch);

        Search allTeamsSearch = new Search();
        this.totalTeams = teamService.countInstances(allTeamsSearch);

        Search activeTeamSearch = new Search();
        activeTeamSearch.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        this.activeTeams = teamService.countInstances(activeTeamSearch);


        Search departmentSearch = new Search();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            departmentSearch.addFilterILike("departmentName", "%" + searchTerm + "%");
        }
        if (selectedStatus != null) {
            departmentSearch.addFilterEqual("recordStatus", selectedStatus);
        }
        if (createdFrom != null) {
            departmentSearch.addFilterGreaterOrEqual("dateCreated", createdFrom);
        }
        if (createdTo != null) {
            departmentSearch.addFilterLessOrEqual("dateCreated", createdTo);
        }


        // Filter and load dataModels based on search/filter criteria
        this.departmentModels = departmentService.getInstances(departmentSearch, 0, 1000);

        // Setting the teamsCount for each department
        for (Department department : this.departmentModels) {
            int count = teamService.getTeamsByDepartment(department).size();
            department.setTeamsCount(count);
        }
    }

    public void deleteSelectedDepartment(Department department) {
        // Implement delete logic
        try {
            departmentService.deleteInstance(department);
            reloadFilterReset();
        } catch (org.sers.webutils.model.exception.OperationFailedException e) {
            // Handle exception (e.g., log or show error message)
            e.printStackTrace();
        }
    }


    public int getTeamsCount(Department department) {
        return teamService.getTeamsByDepartment(department).size();
    }

    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }
}