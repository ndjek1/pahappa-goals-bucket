package org.pahappa.systems.kpiTracker.views.organizationstructure;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.logging.Logger;

@ManagedBean(name = "departmentFormDialog")
@Getter
@Setter
@SessionScoped
public class DepartmentFormDialog extends DialogForm<Department> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DepartmentFormDialog.class.getSimpleName());
    private DepartmentService departmentService;


    @PostConstruct
    public void init() {
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
    }

    public DepartmentFormDialog() {
        super("DepartmentFormDialog", 700, 300);
    }

    private Department department = new Department();

    @ManagedProperty("#{departmentsView}")
    private OrganizationStructureView departmentsView;

    @Override
    public void persist() {
        save();
    }

    public void save() {
        try {
            departmentsView.getDepartmentService().saveInstance(department);
            departmentsView.reloadFilterReset();
        } catch (org.sers.webutils.model.exception.ValidationFailedException | org.sers.webutils.model.exception.OperationFailedException e) {
            e.printStackTrace();
        }
        department = new Department();  //resetting for the next use
    }

    public void show() {
        department = new Department();
    }


    public Department getModel() {
        return department;
    }
    public void setModel(Department model) {
        this.department = model != null ? model : new Department();
    }


}
