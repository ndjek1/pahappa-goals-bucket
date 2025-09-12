package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.TeamService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.sers.webutils.client.views.presenters.PaginatedTableView;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.*;

@ManagedBean(name = "departmentsView")
@Getter
@Setter
@SessionScoped
public class OrganizationStructureView extends PaginatedTableView<Department, OrganizationStructureView, OrganizationStructureView> {

    private DepartmentService departmentService;
    private TeamService teamService;
    private UserService userService;
    private StaffService staffService;

    private List<RecordStatus> recordStatusList;

    private int totalDepartments;
    private int totalTeams;
    private int totalMembers;
    private int activeDepartments;
    private int activeTeams;

    private String searchTerm;
    Search search ;
    private RecordStatus selectedStatus;
    private Date createdFrom, createdTo;

    private List<Team> teamModels;
    private String dataEmptyMessage = "No departments found.";

    private  boolean savedDepartment;
    private boolean editedDepartment;

    @PostConstruct
    public void init() {
        departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        teamService = ApplicationContextProvider.getBean(TeamService.class);
        userService = ApplicationContextProvider.getBean(UserService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.recordStatusList = Arrays.asList(RecordStatus.values());

        reloadFilterReset();
    }

    @Override
    public void reloadFromDB(int first, int pageSize, Map<String, Object> filters) throws Exception {

        super.setDataModels(departmentService.getInstances(this.search, first, pageSize));
        super.setTotalRecords(departmentService.countInstances(this.search));

        // attach teamsCount to each department
        for (Department department : this.getDataModels()) {
            int count = teamService.getTeamsByDepartment(department).size();
            department.setTeamsCount(count);
            department.setMemberCount(staffService.getMembersByTeam(department).size());
        }
    }

    @Override
    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }

    @Override
    public String getFileName() {
        return "departments_export";
    }

    @Override
    public List load(int first, int pageSize, Map filters, Map sortOrders) {
        // Not used if reloadFromDB is implemented, but required by base class
        return null;
    }

    @Override
    public void reloadFilterReset() {
        this.search = new Search(Department.class);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            search.addFilterILike("departmentName", "%" + searchTerm + "%");
        }
        if (selectedStatus != null) {
            search.addFilterEqual("recordStatus", selectedStatus);
        }
        if (createdFrom != null) {
            search.addFilterGreaterOrEqual("dateCreated", createdFrom);
        }
        if (createdTo != null) {
            search.addFilterLessOrEqual("dateCreated", createdTo);
        }
        super.setTotalRecords(departmentService.countInstances(search));
        try {
            super.reloadFilterReset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSuccessMessage() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (this.savedDepartment) {
            // Message for creating a new department
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department created successfully."));
            this.savedDepartment = false; // Reset the flag
        }

        if (this.editedDepartment) {
            // Message for updating an existing department
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department updated successfully."));
            this.editedDepartment = false; // Reset the flag
        }
    }

    public void deleteSelectedDepartment(Department department) {
        try {
            departmentService.deleteInstance(department);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Department deleted successfully."));
            reloadFilterReset();
        } catch (OperationFailedException e) {
            e.printStackTrace();
        }
    }

}
