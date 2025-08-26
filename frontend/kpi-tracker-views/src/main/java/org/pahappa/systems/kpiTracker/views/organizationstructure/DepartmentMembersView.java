package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.server.core.service.excel.reports.ExcelReport;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "departmentMembersView")
@SessionScoped
@Getter
@Setter
public class DepartmentMembersView implements Serializable {

    private static final long serialVersionUID = 1L;

    private StaffService staffService;
    private Department selectedDepartment;
    private List<Staff> departmentMembers;

    private String searchTerm;


    private boolean membersDialogVisible;


    @PostConstruct
    public void init() {
        staffService = ApplicationContextProvider.getBean(StaffService.class);
    }

    public void show(Department department) {
        this.selectedDepartment = department;
        if (department != null) {
            this.departmentMembers = staffService.getMembersByDepartment(department);
        } else {
            this.departmentMembers = null;
        }
    }

    public String goToDepartmentMembersView(Department department) {
        this.selectedDepartment = department;
        if (department != null) {
            this.departmentMembers = staffService.getMembersByDepartment(department);
        } else {
            this.departmentMembers = null;
        }
        membersDialogVisible = true;
        return "ManageMembers?faces-redirect=true";
    }

    public String goToDepartmentMembersView() {
        return goToDepartmentMembersView(this.selectedDepartment);
    }

    public void reloadFilterReset() {

        Search departmentMembersSearch = new Search();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            departmentMembersSearch.addFilterILike("firstName", "%" + searchTerm + "%");
            departmentMembersSearch.addFilterILike("lastName", "%" + searchTerm + "%");
        }


        // Filter and load dataModels based on search/filter criteria
        this.departmentMembers = staffService.getInstances(departmentMembersSearch, 0, 0);

    }

    public void deleteSelectedDepartment(Staff staff) {
        try {
            staffService.deleteInstance(staff);
            reloadFilterReset();
        } catch (org.sers.webutils.model.exception.OperationFailedException e) {
            e.printStackTrace();
        }
    }

    public List<ExcelReport> getExcelReportModels() {
        return Collections.emptyList();
    }


}