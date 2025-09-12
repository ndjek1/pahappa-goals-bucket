package org.pahappa.systems.kpiTracker.views.organizationstructure;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.organization_structure.Team;
import org.pahappa.systems.kpiTracker.models.security.RoleConstants;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.Role;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.service.UserService;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean(name = "departmentFormDialog")
@Getter
@Setter
@SessionScoped
public class DepartmentFormDialog extends DialogForm<Department> {

    private static final long serialVersionUID = 1L;
    private DepartmentService departmentService;
    private List<Staff> availableStaffs = new ArrayList<>();
    private StaffService staffService;
    private UserService userService;
    private Staff selectedStaff = new Staff();



    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.userService = ApplicationContextProvider.getBean(UserService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        loadAvailableStaffs();
    }

    public DepartmentFormDialog() {
        super("DepartmentFormDialog", 550, 300);
    }


    @ManagedProperty("#{departmentsView}")
    private OrganizationStructureView departmentsView;

    @Override
    public void persist() {
        save();
    }

    public void save() {
        try {
            departmentsView.getDepartmentService().saveInstance(this.model);
            if(this.model.getDepartmentHead() != null){
                this.model.getDepartmentHead().addRole(userService.getRoleByRoleName(RoleConstants.ROLE_DEPARTMENT_LEAD));
                userService.saveUser(this.model.getDepartmentHead());
            }
            departmentsView.reloadFilterReset();
            hide();
        } catch (org.sers.webutils.model.exception.ValidationFailedException | org.sers.webutils.model.exception.OperationFailedException e) {
            e.printStackTrace();
        }
        resetModal(); //resetting for the next use
    }



    public void  loadAvailableStaffs() {
        if (this.staffService != null) {
            Search search = new Search(Staff.class);
            search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);


            if (isEditing && this.model != null && this.model.getId() != null) {
                search.addFilterOr(
                        Filter.isNull("department"),
                        Filter.equal("department.id", this.model.getId())
                );
            } else {
                // For new departments, only show unassigned staff.
                search.addFilter(Filter.isNull("department"));
            }
            this.availableStaffs = staffService.getInstances(search, 0, 0);
        } else {
            this.availableStaffs = new ArrayList<>();
        }

    }


    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (this.model != null) {
            isEditing = true;
            loadAvailableStaffs();
        }
    }

    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new Department();
        isEditing = false;
    }

}
