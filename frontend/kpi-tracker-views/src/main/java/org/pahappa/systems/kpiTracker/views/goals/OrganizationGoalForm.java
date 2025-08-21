package org.pahappa.systems.kpiTracker.views.goals;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
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
        super(HyperLinks.ORGANIZATION_GOAL_DIALOG, 500, 300);
    }

    @PostConstruct
    public void init() {
        this.organizationGoalService = ApplicationContextProvider.getBean(OrganizationGoalService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        loadReviewCycle();
    }

    @Override
    public void persist() throws Exception {
        if (model.getName() == null) {
            UiUtils.showMessageBox("Missing goal name","Goal must have a type.");
            return;
        }

        if(this.reviewCycle == null){
            UiUtils.showMessageBox("No active review cycle","There is no running review cycle!");
            return;
        }
        Search search = new Search();
        search.addFilterAnd(
                Filter.equal("reviewCycle.id", this.reviewCycle.getId()),
                Filter.equal("recordStatus", RecordStatus.ACTIVE)
        );
        double totalWeight = this.organizationGoalService.getInstances(search,0,0).stream()
                .mapToDouble(OrganizationGoal::getContributionWeight)
                .sum();
        if (totalWeight + super.model.getContributionWeight() > 100) {
            UiUtils.showMessageBox("Total contribution weight too high","Sum of all goals contribution weights is greater than 100");
            return;
        }

        model.setReviewCycle(this.reviewCycle);
        organizationGoalService.saveInstance(super.model);
    }

    public void loadReviewCycle(){
        Search search = new Search();
        search.addFilterAnd(
                Filter.equal("status", ReviewCycleStatus.ACTIVE),
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
