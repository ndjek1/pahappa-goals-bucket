package org.pahappa.systems.kpiTracker.views.dialogs.systemSetup;


import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.GlobalWeightService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.core.services.systemSetupService.ReviewCycleService;
import org.pahappa.systems.kpiTracker.models.systemSetup.GlobalWeight;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.RatingCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "orgFitCategoryForm")
@Getter
@Setter
@SessionScoped
public class OrgFitCategoryForm extends DialogForm<OrgFitCategory> {
    private OrgFitCategoryService orgFitCategoryService;
    private GlobalWeightService globalWeightService;
    private ReviewCycleService reviewCycleService;
    private double globalWeight;
    private List<RatingCategory> ratingCategories;


    public OrgFitCategoryForm() {
        super(HyperLinks.ORG_FIT_CATEGORY_DIALOG, 500, 350);
    }

    @PostConstruct
    public void init() {
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        this.globalWeightService = ApplicationContextProvider.getBean(GlobalWeightService.class);
        this.reviewCycleService = ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.ratingCategories = Arrays.asList(RatingCategory.values());
        loadGlobalWeight();
    }

    @Override
    public void persist() throws Exception {
        double existingWeights = orgFitCategoryService.getAllInstances().stream()
                .filter(c -> !c.getId().equals(this.model.getId())) // exclude current model
                .mapToDouble(OrgFitCategory::getWeight)
                .sum();

        double totalWeight = existingWeights + this.model.getWeight();

        if (totalWeight <= globalWeight) {
            orgFitCategoryService.saveInstance(super.model);
            resetModal();
            hide();
        } else {
            UiUtils.ComposeFailure("Weight limit reached", "The weights don't match configuration");
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
