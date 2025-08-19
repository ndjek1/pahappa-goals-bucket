package org.pahappa.systems.kpiTracker.views.goals;

import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.organization_structure.Department;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleType;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "organizationGoalForm")
@SessionScoped
public class OrganizationGoalForm extends DialogForm<OrganizationGoal> {

    private static final long serialVersionUID = 1L;
    private OrganizationGoalService organizationGoalService;
    private ReviewCycleService reviewCycleService;
    private ReviewCycle reviewCycle ;


    public OrganizationGoalForm() {
        super(HyperLinks.ORGANIZATION_GOAL_DIALOG, 500, 600);
    }

    @PostConstruct
    public void init() {
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.reviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }

    @Override
    public void persist() throws Exception {
        if (model.getName() == null) {
            UiUtils.showMessageBox("Missing goal name","Goal must have a type.");
            return;
        }

        model.setReviewCycle(this.reviewCycle);
        organizationGoalService.saveInstance(super.model);
    }




    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrganizationGoal();
    }


}
