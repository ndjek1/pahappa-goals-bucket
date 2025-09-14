package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.organization_structure_services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.goals.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goals.GoalStatus;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
    private OrganizationGoal selectedOrganizationGoal;
    private ReviewCycleService reviewCycleService;
    private Department department;
    private User loggedinUser;

    public DepartmentGoalsForm() {
        super(HyperLinks.DEPARTMENT_GOAL_DIALOG, 700, 400);
    }

    @PostConstruct
    public void init() {
        this.departmentGoalService = ApplicationContextProvider.getBean(DepartmentGoalService.class);
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.departmentService = ApplicationContextProvider.getBean(DepartmentService.class);
        this.reviewCycleService =  ApplicationContextProvider.getBean(ReviewCycleService.class);
        loggedinUser = SharedAppData.getLoggedInUser();

        loadOrganizationGoals();
    }

    @Override
    public void persist() throws Exception {
        Validate.notNull(this.model,"Missing required goal details");
        Search search = new Search(DepartmentGoal.class);
        search.addFilterAnd(
                Filter.equal("organizationGoal.id", this.selectedOrganizationGoal.getId()),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );
        if(this.model.getId() != null) {
            search.addFilterNotEqual("id", this.model.getId());
        }
        double totalWeight = this.departmentGoalService.getInstances(search,0,0).stream()
                .mapToDouble(DepartmentGoal::getContributionWeight)
                .sum();
        if (totalWeight + super.model.getContributionWeight() > 100) {
            UiUtils.showMessageBox("Total contribution weight too high","Sum of all goals contribution weights is greater than 100");
            return;
        }
        loadDepartment();

        model.setDepartment(department);
        model.setOrganizationGoal(this.selectedOrganizationGoal);
        model.setStatus(GoalStatus.PENDING);
        departmentGoalService.saveInstance(super.model);
        resetModal();
        hide();

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

    public void loadOrganizationGoals() {
        this.organizationGoals = this.organizationGoalService.getAllInstances();
    }


    @Override
    public void resetModal() {
        super.resetModal();
        this.selectedOrganizationGoal = new OrganizationGoal();
        super.model = new DepartmentGoal();
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if (super.model != null && super.model.getId() != null) {
            isEditing = true;

            // Ensure department is set
            this.department = super.model.getDepartment();

            // Preselect the linked org goal for UI
            if (super.model.getOrganizationGoal() != null) {
                this.selectedOrganizationGoal = super.model.getOrganizationGoal();
            }
        } else {
            isEditing = false;
            loadDepartment(); // set department for new goal
        }
    }


}
