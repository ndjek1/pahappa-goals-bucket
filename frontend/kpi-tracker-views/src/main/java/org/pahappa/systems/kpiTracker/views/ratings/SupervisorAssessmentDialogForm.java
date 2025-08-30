package org.pahappa.systems.kpiTracker.views.ratings;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import lombok.Getter;
import lombok.Setter;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryItemService;
import org.pahappa.systems.kpiTracker.core.services.OrgFitCategoryService;
import org.pahappa.systems.kpiTracker.core.services.impl.ReviewCycleService;
import org.pahappa.systems.kpiTracker.core.services.ratings.CompanyValueRatingService;
import org.pahappa.systems.kpiTracker.core.services.ratings.KeeperTestRatingService;
import org.pahappa.systems.kpiTracker.core.services.ratings.SupervisorAssessmentService;
import org.pahappa.systems.kpiTracker.core.services.systemUsers.StaffService;
import org.pahappa.systems.kpiTracker.models.rating.CompanyValue;
import org.pahappa.systems.kpiTracker.models.rating.KeeperTest;
import org.pahappa.systems.kpiTracker.models.rating.SupervisorAssessment;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategory;
import org.pahappa.systems.kpiTracker.models.systemSetup.OrgFitCategoryItem;
import org.pahappa.systems.kpiTracker.models.systemSetup.ReviewCycle;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.OrgFitCategoryType;
import org.pahappa.systems.kpiTracker.models.systemSetup.enums.ReviewCycleStatus;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.security.UiUtils;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.pahappa.systems.kpiTracker.views.goals.ItemRating;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.sers.webutils.model.security.User;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;
import org.sers.webutils.server.shared.SharedAppData;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean(name = "supervisorAssessmentForm1")
@Getter
@Setter
@SessionScoped
public class SupervisorAssessmentDialogForm extends DialogForm<SupervisorAssessment> {
    private static final long serialVersionUID = 1L;
    private Staff supervisor;
    private Staff staff;
    private ReviewCycle reviewCycle;
    private User loggedInUser;
    private StaffService staffService;
    private OrgFitCategoryService orgFitCategoryService;
    private OrgFitCategoryItemService orgFitCategoryItemService;
    private CompanyValueRatingService companyValueRatingService;
    private KeeperTestRatingService keeperTestRatingService;
    private OrgFitCategory keeperTest, companyValues;
    private List<OrgFitCategoryItem> keeperTestItems, companyValuesItems;
    private List<ItemRating> keeperTestItemRatings;
    private List<ItemRating> companyValueItemRatings;


    private SupervisorAssessmentService supervisorAssessmentService;
    private ReviewCycleService reviewCycleService;
    public SupervisorAssessmentDialogForm() {
        super(HyperLinks.SUPERVISOR_ASSESSMENT_FORM_DIALOG, 600, 500);
    }

    @PostConstruct
    public void init() {
        this.supervisorAssessmentService =ApplicationContextProvider.getBean(SupervisorAssessmentService.class);
        this.reviewCycleService =ApplicationContextProvider.getBean(ReviewCycleService.class);
        this.staffService = ApplicationContextProvider.getBean(StaffService.class);
        this.orgFitCategoryService = ApplicationContextProvider.getBean(OrgFitCategoryService.class);
        this.orgFitCategoryItemService = ApplicationContextProvider.getBean(OrgFitCategoryItemService.class);
        this.keeperTestRatingService = ApplicationContextProvider.getBean(KeeperTestRatingService.class);
        this.supervisorAssessmentService =  ApplicationContextProvider.getBean(SupervisorAssessmentService.class);
        this.companyValueRatingService =  ApplicationContextProvider.getBean(CompanyValueRatingService.class);
        this.loggedInUser = SharedAppData.getLoggedInUser();
        super.model = new  SupervisorAssessment();
        loadSupervisor();
        loadActiveReviewCycle();
        loadCategories();
        loadItems();
    }

    @Override
    public void persist() throws Exception {
        if(this.supervisor != null && this.staff != null) {
            super.model.setSupervisor(supervisor);
            super.model.setStaff(staff);
        }
        if(this.reviewCycle != null) {
            super.model.setReviewCycle(reviewCycle);
        }
        supervisorAssessmentService.saveInstance(super.model);
        saveKeeperTestRating();
        saveCompanyValuesRating();
        resetModal();
        hide();
    }

    public void saveCompanyValuesRating() throws ValidationFailedException, OperationFailedException {
        CompanyValue companyValuesRating = new CompanyValue();
        if(this.companyValueRatingService != null) {
            companyValuesRating.setSupervisor(this.supervisor);
            companyValuesRating.setStaff(this.staff);
            companyValuesRating.setReviewCycle(this.reviewCycle);
            companyValuesRating.setScore(this.getCompanyValuesScore());
            companyValueRatingService.saveInstance(companyValuesRating);
        }
    }

    public void saveKeeperTestRating() throws ValidationFailedException, OperationFailedException {
        KeeperTest keeperTestRating = new KeeperTest();
        if(this.keeperTestRatingService != null) {
            keeperTestRating.setSupervisor(this.supervisor);
            keeperTestRating.setStaff(this.staff);
            keeperTestRating.setReviewCycle(this.reviewCycle);
            keeperTestRating.setScore(this.getKeeperTestScore());
            keeperTestRatingService.saveInstance(keeperTestRating);
        }
    }
    public void loadActiveReviewCycle() {
        this.reviewCycle = this.reviewCycleService.searchUniqueByPropertyEqual("status", ReviewCycleStatus.ACTIVE);
    }
    public  void loadSupervisor(){
        if(this.loggedInUser != null) {
            this.supervisor = staffService.searchUniqueByPropertyEqual("user.id",loggedInUser.getId());
        }
    }

    public void loadCategories(){
        if(this.orgFitCategoryService != null) {
            this.keeperTest = orgFitCategoryService.searchUniqueByPropertyEqual("ratingCategory", OrgFitCategoryType.KEEPER_TEST);
            this.companyValues = orgFitCategoryService.searchUniqueByPropertyEqual("ratingCategory", OrgFitCategoryType.COMPANY_VALUES);
        }
    }


    public void loadItems(){
        if(this.keeperTest != null) {
            Search search = new Search(OrgFitCategoryItem.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus", RecordStatus.ACTIVE),
                    Filter.equal("orgFitCategory.id",keeperTest.getId())
            );
            this.keeperTestItems = orgFitCategoryItemService.getInstances(search,0,0);
            // Wrap into ItemRatings
            this.keeperTestItemRatings = keeperTestItems.stream()
                    .map(item -> new ItemRating(item, null)) // rating initially null
                    .collect(Collectors.toList());
        }
        if(this.companyValues != null) {
            Search search = new Search(OrgFitCategoryItem.class);
            search.addFilterAnd(
                    Filter.equal("recordStatus",RecordStatus.ACTIVE),
                    Filter.equal("orgFitCategory.id",companyValues.getId())
            );
            this.companyValuesItems = orgFitCategoryItemService.getInstances(search,0,0);
            this.companyValueItemRatings = companyValuesItems.stream()
                    .map(item -> new ItemRating(item, null))
                    .collect(Collectors.toList());
        }
    }


    public int getKeeperTestScore() {
        int total = keeperTestItemRatings.stream()
                .filter(ir -> ir.getRating() != null)
                .mapToInt(ItemRating::getRating)
                .sum();

        return Math.round((float) total / keeperTestItems.size());
    }

    public int getCompanyValuesScore() {
        int total = companyValueItemRatings.stream()
                .filter(ir -> ir.getRating() != null)
                .mapToInt(ItemRating::getRating)
                .sum();

        return Math.round((float) total / companyValueItemRatings.size());
    }


    public int getOverallScore() {
        // average of the two category averages (or weight them if needed)
        int k = getKeeperTestScore();
        int c = getCompanyValuesScore();
        if (k == 0 && c == 0) return 0;
        // simple mean of available categories
        int count = 0; if (k > 0) count++; if (c > 0) count++;
        return Math.round((float) (k + c) / Math.max(count, 1));
    }




    @Override
    public void resetModal() {
        super.resetModal();
        super.model = new SupervisorAssessment();
    }
}
