package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;


import org.pahappa.systems.kpiTracker.core.services.GlobalWeightService;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

@ManagedBean(name = "orgFitCategoryForm")
@SessionScoped
public class OrgFitCategoryForm extends DialogForm<OrgFitCategory> {
    private OrgFitCategoryService orgFitCategoryService;
    private GlobalWeightService globalWeightService;
    private ReviewCycleService reviewCycleService;
    private double globalWeight;


    public OrgFitCategoryForm() {
        super(HyperLinks.ORG_FIT_CATEGORY_DIALOG, 500, 350);
    }

    @PostConstruct
    public void init() {
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        this.globalWeightService = ApplicationContextProvider.getBean(GlobalWeightService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        loadGlobalWeight();
    }

    @Override
    public void persist() throws Exception {

        double totalWeight = orgFitCategoryService.getAllInstances().stream()
                .mapToDouble(OrgFitCategory::getWeight)
                .sum();

        if (totalWeight + super.model.getWeight() <= globalWeight) {
            orgFitCategoryService.saveInstance(super.model);
            resetModal();
            hide();
        }else {
            UiUtils.ComposeFailure("Weight limit reached","The weight don't match Configuration");
            resetModal();
            return;
        }

    }

    public GlobalWeight getGlobalWeightForActiveCycle() {
        ReviewCycle activeReviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
        return this.globalWeightService.searchUniqueByPropertyEqual("reviewCycle", activeReviewCycle);
    }
    public void loadGlobalWeight(){
        if(getGlobalWeightForActiveCycle() != null){
            this.globalWeight = getGlobalWeightForActiveCycle().getOrgFitWeight();
        }else {
            UiUtils.showMessageBox("Weight limit reached","The weight don't match config");
        }
    }


    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new OrgFitCategory();
    }
}
