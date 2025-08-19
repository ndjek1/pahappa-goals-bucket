package org.pahappa.systems.kpiTracker.views.goals;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean(name = "departmentGoalsForm")
@Getter
@Setter
@SessionScoped
public class DepartmentGoalsForm extends DialogForm<DepartmentGoal> {

    private static final long serialVersionUID = 1L;
    private DepartmentGoalService departmentGoalService;
    private OrganizationGoalService organizationGoalService;
    private List<OrganizationGoal> organizationGoals;
    private DepartmentService departmentService;
    private Department department;
    private User loggedinUser;

    public DepartmentGoalsForm() {
        super(HyperLinks.DEPARTMENT_GOAL_DIALOG, 500, 400);
    }

    @PostConstruct
    public void init() {
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.organizationGoals = this.organizationGoalService.getAllInstances();
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        loggedinUser = SharedAppData.getLoggedInUser();
        loadDepartment();
    }

    @Override
    public void persist() throws Exception {
        if (model.getName() == null) {
            UiUtils.showMessageBox("Missing goal name","Goal must have a type.");
            return;
        }
        model.setDepartment(department);
        model.setStatus(GoalStatus.PENDING);
        departmentGoalService.saveInstance(super.model);
    }

    public void loadDepartment() {
        if (loggedinUser.hasRole("DEPT_LEAD")) {
            this.department = departmentService.getAllInstances()
                    .stream()
                    .filter(d -> d.getDepartmentHead() != null
                            && d.getDepartmentHead().equals(loggedinUser))
                    .findFirst()
                    .orElse(null);
        }
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new DepartmentGoal();
    }
}
