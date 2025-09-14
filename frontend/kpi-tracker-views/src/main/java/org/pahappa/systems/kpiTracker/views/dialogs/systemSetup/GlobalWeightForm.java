package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;

import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.GlobalWeightService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@ManagedBean(name = "globalWeightsForm")
@Getter
@Setter
@SessionScoped
public class GlobalWeightForm extends DialogForm<GlobalWeight> {
    private static final long serialVersionUID = 1L;

    private GlobalWeightService globalWeightService;
    private List<ReviewCycle> reviewCycleList;
    private ReviewCycleService reviewCycleService;
    private ReviewCycle activeReviewCycle;

    public GlobalWeightForm() {
        super(HyperLinks.GLOBAL_WEIGHT_DIALOG, 500, 400);
    }

    @PostConstruct
    public void init() {
        globalWeightService = ApplicationContextProvider.getBean(GlobalWeightService.class);
        reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        loadReviewCycles();
        loadActiveReviewCycle();
    }

    @Override
    public void persist() throws Exception {
        if(this.activeReviewCycle != null) {
            super.model.setReviewCycle(this.activeReviewCycle);
            globalWeightService.saveInstance(super.model);
        }

    }


    @Override
    public void resetModal() {
        super.resetModal();
        loadReviewCycles();
        super.model = new GlobalWeight();
    }

    // In your globalWeightsForm backing bean
    public void loadReviewCycles() {
        this.reviewCycleList = reviewCycleService.getAllInstances();
    }

    public void loadActiveReviewCycle() {
        this.activeReviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }

    public void updateMboWeight() {
        // A primitive double cannot be null; an empty field defaults to 0.0.
        double orgFit = model.getOrgFitWeight();

        // Only calculate if the input is within a valid range.
        if (orgFit >= 0 && orgFit <= 100) {
            model.setMboWeight(100.0 - orgFit);
        }
    }

    public void updateOrgFitWeight() {
        double mbo = model.getMboWeight();

        // Only calculate if the input is within a valid range.
        if (mbo >= 0 && mbo <= 100) {
            model.setOrgFitWeight(100.0 - mbo);
        }
    }


}
