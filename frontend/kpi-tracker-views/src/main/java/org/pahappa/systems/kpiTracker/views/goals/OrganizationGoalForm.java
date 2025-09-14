package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.goals.OrganizationGoalService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.goals.OrganizationGoal;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.utils.Validate;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@ManagedBean(name = "organizationGoalForm")
@Getter
@Setter
@SessionScoped
public class OrganizationGoalForm extends DialogForm<OrganizationGoal> {

    private static final long serialVersionUID = 1L;
    private OrganizationGoalService organizationGoalService;
    private ReviewCycleService reviewCycleService;
     private   List<ReviewCycle> reviewCycles;
    private ReviewCycle reviewCycle ;


    public OrganizationGoalForm() {
        super(HyperLinks.ORGANIZATION_GOAL_DIALOG, 500, 380);
    }

    @PostConstruct
    public void init() {
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        loadReviewCycle();
    }

    @Override
    public void persist() throws Exception {
       Validate.notNull(this.model,"Missing goal details");
        organizationGoalService.saveInstance(super.model);
        resetModal();
        hide();
    }

    public void loadReviewCycle(){
        Search search = new Search();
        search.addFilterAnd(
                Filter.notEqual("status", ReviewCycleStatus.ENDED),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );
        this.reviewCycles = this.reviewCycleService.getInstances(search,0,0);
    }



    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrganizationGoal();
        loadReviewCycle();
    }


}
